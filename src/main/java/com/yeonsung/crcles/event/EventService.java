package com.yeonsung.crcles.event;

import com.yeonsung.crcles.account.Account;
import com.yeonsung.crcles.club.Club;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    /*
    * 모임생성
    * */
    public Event createEvent(Event event, Club club, Account account) {
        event.setCreateBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setClub(club);
        return eventRepository.save(event);
    }

}
