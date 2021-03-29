package com.yeonsung.crcles.club.event;

import com.yeonsung.crcles.club.Club;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Async
@Component
@Transactional(readOnly = true)
public class ClubEventListener {

    @EventListener
    public void handleClubCreatedEvent(ClubCreatedEvent clubCreatedEvent) {
        Club club = clubCreatedEvent.getClub();
        log.info(club.getTitle() + "is created.");
        // TODO 이메일 보내거나, DB에 Notification 정보를 저장하면 됩니다.
        throw new RuntimeException();
    }

}
