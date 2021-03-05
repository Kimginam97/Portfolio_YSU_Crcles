package com.yeonsung.crcles.account.form;

import lombok.Data;

@Data
public class NotificationsForm {

    private boolean circlesCreatedByEmail;  // 동아리 생성 이메일 알람

    private boolean circlesCreatedByWeb;    // 동아리 생성 웹 알람

    private boolean circlesEnrollmentResultByEmail; // 동아리 등록 이메일 알람

    private boolean circlesEnrollmentResultByWeb; // 동아리 등록 웹 알람

    private boolean circlesUpdatedByEmail;  // 동아리 변경 이메일 알람

    private boolean circlesUpdatedByWeb;    // // 동아리 변경 웹 알람

}
