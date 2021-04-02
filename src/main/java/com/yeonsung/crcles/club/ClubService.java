package com.yeonsung.crcles.club;

import com.yeonsung.crcles.account.Account;
import com.yeonsung.crcles.club.event.ClubCreatedEvent;
import com.yeonsung.crcles.club.event.ClubUpdateEvent;
import com.yeonsung.crcles.club.form.ClubDescriptionForm;
import com.yeonsung.crcles.tag.Tag;
import com.yeonsung.crcles.tag.TagRepository;
import com.yeonsung.crcles.zone.Zone;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static com.yeonsung.crcles.club.form.ClubForm.VALID_PATH_PATTERN;

@Service
@Transactional
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

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
        checkIfManager(account,club);

        // 동아리 객체 반환
        return club;
    }

    public Club getClub(String path) {
        Club club = this.clubRepository.findByPath(path);
        checkIfExistingClub(path,club);

        return club;
    }

    public void updateClubDescription(Club club, ClubDescriptionForm clubDescriptionForm) {
        modelMapper.map(clubDescriptionForm, club);
        eventPublisher.publishEvent(new ClubUpdateEvent(club,"동아리 소개를 수정했습니다"));
    }

    // 동아리 이미지 수정
    public void updateClubImage(Club club, String image) {
        club.setImage(image);
    }

    // 배너활성화
    public void enableClubBanner(Club club) {
        club.setUseBanner(true);
    }

    // 배너비활성화
    public void disableClubBanner(Club club) {
        club.setUseBanner(false);
    }


    /*
    * 태그 추가
    * 태그 삭제
    * 태그와 회원매니저정보 가져오기
    * */
    public void addTag(Club club, Tag tag) {
        club.getTags().add(tag);
    }

    public void removeTag(Club club, Tag tag) {
        club.getTags().remove(tag);
    }

    public Club getClubToUpdateTag(Account account, String path) {
        Club club = clubRepository.findClubWithTagsByPath(path);
        checkIfExistingClub(path, club);
        checkIfManager(account, club);
        return club;
    }


    /*
    * 지역 추가
    * 지역 삭제
    * 지역과 회원매니저정보 가져오기
    * */
    public void addZone(Club club, Zone zone) {
        club.getZones().add(zone);
    }

    public void removeZone(Club club, Zone zone) {
        club.getZones().remove(zone);
    }

    public Club getClubToUpdateZone(Account account, String path) {
        Club club = clubRepository.findClubWithZonesByPath(path);
        checkIfExistingClub(path, club);
        checkIfManager(account, club);
        return club;
    }



    private void checkIfManager(Account account, Club club) {
        if (!account.isManagerOf(club)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }

    private void checkIfExistingClub(String path, Club club) {
        if (club == null) {
            throw new IllegalArgumentException(path + "에 해당하는 스터디가 없습니다.");
        }
    }


    /*
    * 동아리 모집 상태
    * 동아리 공개
    * 동아리 종료
    * 동아리 모집시작
    * 동아리 모집종료
    * */
    public Club getStudyToUpdateStatus(Account account, String path) {
        Club club = clubRepository.findClubWithManagersByPath(path);
        checkIfExistingClub(path, club);
        checkIfManager(account, club);
        return club;
    }

    public void publish(Club club) {
        club.publish();
        this.eventPublisher.publishEvent(new ClubCreatedEvent(club));
    }

    public void close(Club club) {
        club.close();
        eventPublisher.publishEvent(new ClubUpdateEvent(club,"동아리를 종료했습니다"));
    }

    public void startRecruit(Club club) {
        club.startRecruit();
        eventPublisher.publishEvent(new ClubUpdateEvent(club,"동아리 팀원 모집을 시작합니다"));
    }

    public void stopRecruit(Club club) {
        club.stopRecruit();
        eventPublisher.publishEvent(new ClubUpdateEvent(club,"동아리 팀원모집을 중단했습니다"));
    }


    /*
    * Path(경로) 검증후 가져온다
    * Path(경로) 수정
    * 제목 검증
    * 제목 수정
    * 동아리 삭제
    * */

    public boolean isValidPath(String newPath) {
        if (!newPath.matches(VALID_PATH_PATTERN)) {
            return false;
        }

        return !clubRepository.existsByPath(newPath);
    }

    public void updateClubPath(Club club, String newPath) {
        club.setPath(newPath);
    }

    public boolean isValidTitle(String newTitle) {
        return newTitle.length() <= 50;
    }

    public void updateClubTitle(Club club, String newTitle) {
        club.setTitle(newTitle);
    }

    public void remove(Club club) {
        if (club.isRemovable()) {
            clubRepository.delete(club);
        } else {
            throw new IllegalArgumentException("동아리를 삭제할 수 없습니다.");
        }
    }

    /*
    * 동아리 회원참가
    * 동아리 회원탈퇴
    * */

    public void addMember(Club club, Account account) {
        club.addMember(account);
    }

    public void removeMember(Club club, Account account) {
        club.removeMember(account);
    }

    // 동아리 정보 가져오기
    public Club getClubToEnroll(String path) {
        Club club = clubRepository.findClubOnlyByPath(path);
        checkIfExistingClub(path, club);
        return club;
    }

}
