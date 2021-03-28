package com.yeonsung.modules.event;

import com.yeonsung.modules.account.Account;
import com.yeonsung.modules.account.UserAccount;
import com.yeonsung.modules.club.Club;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NamedEntityGraph(
        name = "Event.withEnrollments",
        attributeNodes = @NamedAttributeNode("enrollments")
)
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Event {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Club club;  // 동아리

    @ManyToOne
    private Account createdBy;   // 모임생성자

    @Column(nullable = false)
    private String title;   // 제목

    @Lob
    private String description; // 설명

    @Column(nullable = false)
    private LocalDateTime createdDateTime;  // 모임접수 생성날짜

    @Column(nullable = false)
    private LocalDateTime endEnrollmentDateTime;    // 모임접수 끝난날짜

    @Column(nullable = false)
    private LocalDateTime startDateTime;    // 모임 시작한 날짜

    @Column(nullable = false)
    private LocalDateTime endDateTime;  // 모임 끝난 날짜

    @Column
    private Integer limitOfEnrollments; // 등록 인원수

    @OneToMany(mappedBy = "event")
    @OrderBy("enrolledAt")
    private List<Enrollment> enrollments = new ArrayList<>();   // 등록

    @Enumerated(EnumType.STRING)
    private EventType eventType;    // 등록하는 방법


    // 모임을 모집중이고 회원이 다른지
    public boolean isEnrollableFor(UserAccount userAccount) {

        return isNotClosed() && !isAttended(userAccount) &&!isAlreadyEnrolled(userAccount);
    }

    // 모임을 모집중이고 회원이 같은지 확인
    public boolean isDisenrollableFor(UserAccount userAccount) {
        return isNotClosed() && !isAttended(userAccount) &&isAlreadyEnrolled(userAccount);
    }

    // 모임 모집중
    private boolean isNotClosed() {
        return this.endEnrollmentDateTime.isAfter(LocalDateTime.now());
    }

    // 모임중 회원이 같고 참여중인지?
    public boolean isAttended(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        for (Enrollment e : this.enrollments) {
            if (e.getAccount().equals(account) && e.isAttended()) {
                return true;
            }
        }

        return false;
    }

    // 모임중 회원이 같은지?
    private boolean isAlreadyEnrolled(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        for (Enrollment e : this.enrollments) {
            if (e.getAccount().equals(account)) {
                return true;
            }
        }
        return false;
    }

    // 모집인원수
    public int numberOfRemainSpots() {
        return this.limitOfEnrollments - (int) this.enrollments.stream().filter(Enrollment::isAccepted).count();
    }

    // 참가인원수
    public long getNumberOfAcceptedEnrollments() {
        return this.enrollments.stream().filter(Enrollment::isAccepted).count();
    }

    /*
    * 연관관계 편의메소드
    * 모임참가
    * 모임취소
    * */
    public void addEnrollment(Enrollment enrollment) {
        this.enrollments.add(enrollment);
        enrollment.setEvent(this);
    }

    public void removeEnrollment(Enrollment enrollment) {
        this.enrollments.remove(enrollment);
        enrollment.setEvent(null);
    }

    // 선착순이고 제한 인원이 넘지 않으면 확정을 지어준다
    public boolean isAbleToAcceptWaitingEnrollment() {
        return this.eventType == EventType.FCFS && this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments();
    }

    // 관리자 수락
    public boolean canAccept(Enrollment enrollment) {
        return this.eventType == EventType.CONFIRMATIVE
                && this.enrollments.contains(enrollment)
                && this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments()
                && !enrollment.isAttended()
                && !enrollment.isAccepted();
    }

    // 관리자 취소
    public boolean canReject(Enrollment enrollment) {
        return this.eventType == EventType.CONFIRMATIVE
                && this.enrollments.contains(enrollment)
                && !enrollment.isAttended()
                && enrollment.isAccepted();
    }

    // 대기인원목록
    private List<Enrollment> getWaitingList() {
        return this.enrollments.stream().filter(enrollment -> !enrollment.isAccepted()).collect(Collectors.toList());
    }

    // 늘어난 모집인원 숫자만큼 늘려준다
    public void acceptWaitingList() {
        if (this.isAbleToAcceptWaitingEnrollment()) {
            var waitingList = getWaitingList();
            int numberToAccept = (int) Math.min(this.limitOfEnrollments - this.getNumberOfAcceptedEnrollments(), waitingList.size());
            waitingList.subList(0, numberToAccept).forEach(e -> e.setAccepted(true));
        }
    }

    // 다음참가자를 확정지워준다
    public void acceptNextWaitingEnrollment() {
        if (this.isAbleToAcceptWaitingEnrollment()) {
            Enrollment enrollmentToAccept = this.getTheFirstWaitingEnrollment();
            if (enrollmentToAccept != null) {
                enrollmentToAccept.setAccepted(true);
            }
        }
    }

    // 첫번째로 대기중인 참가자
    private Enrollment getTheFirstWaitingEnrollment() {
        for (Enrollment e : this.enrollments) {
            if (!e.isAccepted()) {
                return e;
            }
        }

        return null;
    }

    // 참가신청확인
    public void accept(Enrollment enrollment) {
        if (this.eventType == EventType.CONFIRMATIVE
                && this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments()) {
            enrollment.setAccepted(true);
        }
    }

    // 참가신청취소
    public void reject(Enrollment enrollment) {
        if (this.eventType == EventType.CONFIRMATIVE) {
            enrollment.setAccepted(false);
        }
    }

}
