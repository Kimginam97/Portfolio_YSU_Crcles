package com.yeonsung.crcles.account.form;

import lombok.Data;

@Data
public class NotificationsForm {

    private boolean clubCreatedByEmail;  // 동아리 생성 이메일 알람

    private boolean clubCreatedByWeb;    // 동아리 생성 웹 알람

    private boolean clubEnrollmentResultByEmail; // 동아리 등록 이메일 알람

    private boolean clubEnrollmentResultByWeb; // 동아리 등록 웹 알람

    private boolean clubUpdatedByEmail;  // 동아리 변경 이메일 알람

    private boolean clubUpdatedByWeb;    // // 동아리 변경 웹 알람

}
