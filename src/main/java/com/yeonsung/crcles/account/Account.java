package com.yeonsung.crcles.account;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
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

    private LocalDateTime joinedTime; // 가입이된 현재 시간

    private LocalDateTime emailCheckTokenGeneratedTime; // 이메일 토큰 생성 시간

    private String bio; // 짧은 소개

    private String grade;  // 학년

    private String department; // 학과

    private String location;    // 사는 지역

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String profileImage;    // 프로필 이미지


    // 랜덤한 이메일 토큰생성
    public void generateEmailCheckToken() {
        this.emailCheckToken= UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedTime=LocalDateTime.now();
    }

    // 이메일 검증완료 및 현재시간정보 입력
    public void completeSignUpEmail(){
        this.emailVerified=true;
        this.joinedTime=LocalDateTime.now();
    }

    // 토큰값 검증
    public boolean isValidToken(String token){
        return this.emailCheckToken.equals(token);
    }

    // 토큰이 생성되는 시간
    public boolean isSendConfirmEmail() {
        return this.emailCheckTokenGeneratedTime.isBefore(LocalDateTime.now().minusSeconds(1));
    }
}