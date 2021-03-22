package com.yeonsung.crcles.club;

import com.yeonsung.crcles.account.Account;
import com.yeonsung.crcles.account.UserAccount;
import com.yeonsung.crcles.tag.Tag;
import com.yeonsung.crcles.zone.Zone;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NamedEntityGraph(name = "Club.withAll", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("members")})
@NamedEntityGraph(name = "Club.withTagsAndManagers", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("managers")})
@NamedEntityGraph(name = "Club.withZonesAndManagers", attributeNodes = {
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers")})
@NamedEntityGraph(name = "Club.withManagers", attributeNodes = {
        @NamedAttributeNode("managers")})
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Club {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToMany
    private Set<Account> managers = new HashSet<>();

    @ManyToMany
    private Set<Account> members = new HashSet<>();

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    @Column(unique = true)
    private String path;    // path

    private String title;   // 제목

    private String shortDescription;    // 짧은글

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String fullDescription;     // 긴글

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;   // 이미지


    private LocalDateTime publishedDateTime;    // 동아리 공개시간

    private LocalDateTime closedDateTime;       // 동아리 종료시간

    private LocalDateTime recruitingUpdatedDateTime;    // 동아리 인원모집시간

    private boolean recruiting; // 인원모집 여부

    private boolean published;  // 공개 여부

    private boolean closed; // 종료 여부

    private boolean useBanner;  // 배너 여부

    // 동아리 관리자 권한
    public void addManager(Account account) {
        this.managers.add(account);
    }

    // 가입여부 확인
    public boolean isJoinable(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return this.isPublished() && this.isRecruiting()
                && !this.members.contains(account) && !this.managers.contains(account);

    }

    // 회원여부 확인
    public boolean isMember(UserAccount userAccount) {
        return this.members.contains(userAccount.getAccount());
    }

    // 매니저여부 확인
    public boolean isManager(UserAccount userAccount) {
        return this.managers.contains(userAccount.getAccount());
    }

    // 기존 배너이미지
    public String getImage() {
        return image != null ? image : "/images/default_banner.jpg";
    }

    // 동아리 회원추가
    public void addMember(Account account) {
        this.members.add(account);
    }

    // 동아리 출시여부
    public void publish() {
        if (!this.closed && !this.published) {
            this.published = true;
            this.publishedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("동아리를 공개할 수 없는 상태입니다. 동아리를 이미 공개했거나 종료했습니다.");
        }
    }

    // 동아리 종료 여부
    public void close() {
        if (this.published && !this.closed) {
            this.closed = true;
            this.closedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("동아리를 종료할 수 없습니다. 동아리를 공개하지 않았거나 이미 종료한 동아리입니다.");
        }
    }

    // 동아리 인원모집시작여부
    public void startRecruit() {
        if (canUpdateRecruiting()) {
            this.recruiting = true;
            this.recruitingUpdatedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("인원 모집을 시작할 수 없습니다. 동아리를 공개하거나 한 시간 뒤 다시 시도하세요.");
        }
    }

    // 동아리 인원모집종료여부
    public void stopRecruit() {
        if (canUpdateRecruiting()) {
            this.recruiting = false;
            this.recruitingUpdatedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("인원 모집을 멈출 수 없습니다. 동아리를 공개하거나 한 시간 뒤 다시 시도하세요.");
        }
    }

    // 동아리 인원모집 여부
    public boolean canUpdateRecruiting() {
        return this.published && this.recruitingUpdatedDateTime == null || this.recruitingUpdatedDateTime.isBefore(LocalDateTime.now().minusHours(1));
    }

    // 동아리 공개여부
    public boolean isRemovable() {
        return !this.published;
    }

}