package com.yeonsung.crcles.account.form;


import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.persistence.Lob;

@Data
public class ProfileForm {

    @Length(max = 25)
    private String bio; // 짧은 소개

    @Length(max = 25)
    private String grade;  // 학년

    @Length(max = 25)
    private String department; // 학과

    @Length(max = 25)
    private String location;    // 사는 지역

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String profileImage;    // 프로필 이미지

}
