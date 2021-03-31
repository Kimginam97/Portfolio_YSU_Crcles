package com.yeonsung.crcles.club.event;

import com.yeonsung.crcles.club.Club;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ClubUpdateEvent {

    private final Club club;
    private final String message;

}
