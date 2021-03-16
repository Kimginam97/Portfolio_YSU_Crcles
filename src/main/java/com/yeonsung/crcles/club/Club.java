package com.yeonsung.crcles.club;

import com.yeonsung.crcles.account.Account;
import com.yeonsung.crcles.tag.Tag;
import com.yeonsung.crcles.zone.Zone;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    @Column(unique = true)
    private String path;    // path

    private String title;   // 제목

    private String shortDescription;    // 짧은글

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String fullDescription;     // 긴글

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;   // 이미지

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    private LocalDateTime publishedDateTime;    // 동아리 공개시간

    private LocalDateTime closedDateTime;       // 동아리 종료시간

    private LocalDateTime recruitingUpdatedDateTime;    // 동아리 인원모집시간

    private boolean recruiting; // 인원모집 여부

    private boolean published;  // 공개 여부

    private boolean closed; // 종료 여부

    private boolean useBanner;  // 배너 여부

    public void addManager(Account account) {   // 동아리 관리자 권한
        this.managers.add(account);
    }
}