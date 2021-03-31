package com.yeonsung.crcles.event.event;

import com.yeonsung.crcles.account.Account;
import com.yeonsung.crcles.club.Club;
import com.yeonsung.crcles.config.AppProperties;
import com.yeonsung.crcles.event.Enrollment;
import com.yeonsung.crcles.event.Event;
import com.yeonsung.crcles.mail.EmailMessage;
import com.yeonsung.crcles.mail.EmailService;
import com.yeonsung.crcles.notification.Notification;
import com.yeonsung.crcles.notification.NotificationRepository;
import com.yeonsung.crcles.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class EnrollmentEventListener {

    private final NotificationRepository notificationRepository;
    private final AppProperties appProperties;
    private final TemplateEngine templateEngine;
    private final EmailService emailService;

    @EventListener
    public void handleEnrollmentEvent(EnrollmentEvent enrollmentEvent) {
        Enrollment enrollment = enrollmentEvent.getEnrollment();
        Account account = enrollment.getAccount();
        Event event = enrollment.getEvent();
        Club club = event.getClub();

        if (account.isClubEnrollmentResultByEmail()) {
            sendEmail(enrollmentEvent, account, event, club);
        }

        if (account.isClubEnrollmentResultByWeb()) {
            createNotification(enrollmentEvent, account, event, club);
        }
    }

    // 모임 생성 이메일 보내기
    private void sendEmail(EnrollmentEvent enrollmentEvent, Account account, Event event, Club club) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/club/" + club.getEncodedPath() + "/events/" + event.getId());
        context.setVariable("linkName", club.getTitle());
        context.setVariable("message", enrollmentEvent.getMessage());
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject("연성대 동아리, " + event.getTitle() + " 모임 참가 신청 결과입니다.")
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }


    // 모임 생성 웹 알림 보내기
    private void createNotification(EnrollmentEvent enrollmentEvent, Account account, Event event, Club club) {
        Notification notification = new Notification();
        notification.setTitle(club.getTitle() + " / " + event.getTitle());
        notification.setLink("/club/" + club.getEncodedPath() + "/events/" + event.getId());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(enrollmentEvent.getMessage());
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.EVENT_ENROLLMENT);
        notificationRepository.save(notification);
    }

}