package com.yeonsung.crcles.account;

import com.yeonsung.crcles.tag.Tag;
import com.yeonsung.crcles.zone.Zone;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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


    /*
    * 회원가입
    * */
    @Column(unique = true)
    private String email; // 이메일

    @Column(unique = true)
    private String nickname; // 닉네임

    private String password; // 비밀번호

    /*
    * 이메일
    * */

    private boolean emailVerified; // 이메일 인증

    private String emailCheckToken; // 이메일 검증에 사용할 토큰값

    private LocalDateTime joinedTime; // 가입이된 현재 시간

    private LocalDateTime emailCheckTokenGeneratedTime; // 이메일 토큰 생성 시간


    /*
    * 프로필
    * */

    private String bio; // 짧은 소개

    private String grade;  // 학년

    private String department; // 학과

    private String location;    // 사는 지역

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String profileImage;    // 프로필 이미지


    /*
    * 알림
    * */

    private boolean circlesCreatedByEmail;  // 동아리 생성 이메일 알람

    private boolean circlesCreatedByWeb;    // 동아리 생성 웹 알람

    private boolean circlesEnrollmentResultByEmail; // 동아리 등록 이메일 알람

    private boolean circlesEnrollmentResultByWeb; // 동아리 등록 웹 알람

    private boolean circlesUpdatedByEmail;  // 동아리 변경 이메일 알람

    private boolean circlesUpdatedByWeb;    // 동아리 변경 웹 알람

    /*
    * 태그
    * 지역
    * */
    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

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