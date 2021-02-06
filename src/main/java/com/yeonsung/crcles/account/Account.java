package com.yeonsung.crcles.account;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    private Long id; // 식별자

    @Column(unique = true)
    private String email; // 이메일

    @Column(unique = true)
    private String nickname; // 닉네임

    private String password; // 비밀번호

    private boolean emailVerified; // 이메일 인증

    private String emailCheckToken; // 이메일 검증에 사용할 토큰값

    private LocalDateTime joinedAt; // 가입이된 현재 시간

    // 랜덤한 이메일 토큰생성
    public void generateEmailCheckToken() {
        this.emailCheckToken= UUID.randomUUID().toString();
    }
}