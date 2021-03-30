package com.yeonsung.crcles.notification;

import com.yeonsung.crcles.account.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Notification {

    @Id
    @GeneratedValue
    private Long id;

    private String title;   // 제목

    private String link;    // 링크

    private String message; // 메시지

    private boolean checked;    // 체크

    @ManyToOne
    private Account account;    // 회원

    private LocalDateTime createdDateTime; // 생성된날짜

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;  // 알림타입

}
