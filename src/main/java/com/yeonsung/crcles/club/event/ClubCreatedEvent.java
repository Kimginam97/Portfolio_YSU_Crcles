package com.yeonsung.crcles.club.event;

import com.yeonsung.crcles.club.Club;
import lombok.Getter;

@Getter
public class ClubCreatedEvent {

    private Club club;

    public ClubCreatedEvent(Club club) {
        this.club = club;
    }
}
