package com.yeonsung.crcles.event;

import com.yeonsung.crcles.account.Account;
import com.yeonsung.crcles.club.Club;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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
    private Account createBy;   // 모임생성자

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
    private List<Enrollment> enrollments;   // 등록

    @Enumerated(EnumType.STRING)
    private EventType eventType;    // 등록하는 방법

}
