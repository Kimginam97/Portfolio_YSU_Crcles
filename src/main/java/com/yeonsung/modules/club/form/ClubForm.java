package com.yeonsung.modules.club.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class ClubForm {

    public static final String VALID_PATH_PATTERN = "^[ㄱ-ㅎ가-힣a-z0-9A-Z_-]{3,20}$";

    @NotBlank
    @Length(min = 2, max = 20)
    @Pattern(regexp = VALID_PATH_PATTERN)
    private String path;    // url 경로

    @NotBlank
    @Length(max = 50)
    private String title;   // 제목

    @NotBlank
    @Length(max = 100)
    private String shortDescription;    // 짧은소개

    @NotBlank
    private String fullDescription;     // 긴글소개

}
