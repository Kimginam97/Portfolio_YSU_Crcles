package com.yeonsung.modules.event;

import com.yeonsung.modules.account.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Enrollment {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Event event;    // 어떤 이벤트에 대한 참가 신청인지 ?

    @ManyToOne
    private Account account;    // 어떤 회원이 신청했는지?

    private LocalDateTime enrolledAt;   // 등록된 날짜

    private boolean accepted;   // 접근권한

    private boolean attended;   // 참여여부

}
