package com.yeonsung.crcles.club.event;

import com.yeonsung.crcles.account.Account;
import com.yeonsung.crcles.account.AccountPredicates;
import com.yeonsung.crcles.account.AccountRepository;
import com.yeonsung.crcles.club.Club;
import com.yeonsung.crcles.club.ClubRepository;
import com.yeonsung.crcles.config.AppProperties;
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
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class ClubEventListener {

    private final ClubRepository clubRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;

    /// 동아리 생성될때 이벤트
    @EventListener
    public void handleClubCreatedEvent(ClubCreatedEvent clubCreatedEvent) {
        Club club = clubRepository.findClubWithTagsAndZonesById(clubCreatedEvent.getClub().getId());
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndZones(club.getTags(), club.getZones()));
        accounts.forEach(account -> {
            if (account.isClubCreatedByEmail()) {
                sendClubCreatedEmail(club, account,"새로운 동아리가 생겼습니다","연성대 " + club.getTitle() + "동아리가 생겼습니다!");
            }

            if (account.isClubCreatedByWeb()) {
                saveClubCreatedNotification(club, account,club.getShortDescription(),NotificationType.CLUB_CREATED);
            }
        });
    }

    /// 동아리 수정될때 이벤트
    @EventListener
    public void handleClubUpdateEvent(ClubUpdateEvent clubUpdateEvent) {
        Club club = clubRepository.findClubWithManagersAndMembersById(clubUpdateEvent.getClub().getId());
        Set<Account> accounts = new HashSet<>();
        accounts.addAll(club.getManagers());
        accounts.addAll(club.getMembers());

        accounts.forEach(account -> {
            if (account.isClubUpdatedByEmail()) {
                sendClubCreatedEmail(club, account,clubUpdateEvent.getMessage(),"연성대 " + club.getTitle() + "동아리가 생겼습니다!");
            }

            if (account.isClubUpdatedByWeb()) {
                saveClubCreatedNotification(club, account,clubUpdateEvent.getMessage(),NotificationType.CLUB_UPDATED);
            }
        });
    }

    // 웹 알람일때 Notification 에 저장
    private void saveClubCreatedNotification(Club club, Account account, String message, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setTitle(club.getTitle());
        notification.setLink("/club/" + club.getEncodedPath());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(message);
        notification.setAccount(account);
        notification.setNotificationType(notificationType);
        notificationRepository.save(notification);
    }

    // 이메일 알람일때 메일을 보낸다
    private void sendClubCreatedEmail(Club club, Account account, String contextMessage, String emailSubject) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/club/" + club.getEncodedPath());
        context.setVariable("linkName", club.getTitle());
        context.setVariable("message", contextMessage);
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject(emailSubject)
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

}
