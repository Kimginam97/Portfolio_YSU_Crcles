package com.yeonsung.crcles.account;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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

}