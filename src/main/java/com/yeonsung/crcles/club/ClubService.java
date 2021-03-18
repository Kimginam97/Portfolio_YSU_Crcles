package com.yeonsung.crcles.club;

import com.yeonsung.crcles.account.Account;
import com.yeonsung.crcles.club.form.ClubDescriptionForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final ModelMapper modelMapper;

    // 동아리 생성
    public Club createNewClub(Club club, Account account){
        Club newClub = clubRepository.save(club);
        newClub.addManager(account);
        return newClub;
    }


    /*
    * 동아리 수정권한 확인
    * 동아리 가져오기
    * 동아리 짧은글,긴글 수정
    * */
    public Club getClubToUpdate(Account account, String path) {

        // 동아리 가져온다
        Club club = this.getClub(path);

        // 동아리권한을 확인한다
        if (!account.isManagerOf(club)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }

        // 동아리 객체 반환
        return club;
    }

    public Club getClub(String path) {
        Club club = this.clubRepository.findByPath(path);
        if (club == null) {
            throw new IllegalArgumentException(path + "에 해당하는 동아리가 없습니다.");
        }

        return club;
    }

    public void updateClubDescription(Club club, ClubDescriptionForm clubDescriptionForm) {
        modelMapper.map(clubDescriptionForm, club);
    }


}
