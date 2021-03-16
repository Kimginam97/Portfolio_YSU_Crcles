package com.yeonsung.crcles.club;

import com.yeonsung.crcles.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;

    public Club createNewClub(Club club, Account account){
        Club newClub = clubRepository.save(club);
        newClub.addManager(account);
        return newClub;
    }


}
