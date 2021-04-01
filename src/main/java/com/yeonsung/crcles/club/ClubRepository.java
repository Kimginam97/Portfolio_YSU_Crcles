package com.yeonsung.crcles.club;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ClubRepository extends JpaRepository<Club,Long> ,ClubRepositoryExtension{

    boolean existsByPath(String path);  // path 존재하는지 여부

    @EntityGraph(attributePaths = {"tags", "zones", "managers", "members"}, type = EntityGraph.EntityGraphType.LOAD)
    Club findByPath(String path);   // 해당 정보의 path 가져올때 연관된 엔티티 그래프 값도 가져온다

    @EntityGraph(attributePaths = {"tags", "managers"})
    Club findClubWithTagsByPath(String path);    // 동아리 태그와 매니저 권한 가져오기

    @EntityGraph(attributePaths = {"zones", "managers"})
    Club findClubWithZonesByPath(String path);   // 동아리 지역과 매니저 권한만가져오기

    @EntityGraph(attributePaths = "managers")
    Club findClubWithManagersByPath(String path);   // 동아리 매니저 권한만 가져오기

    @EntityGraph(attributePaths = "members")
    Club findClubWithMembersByPath(String path);    // 동아리 회원정보 가져오기

    Club findClubOnlyByPath(String path);

    @EntityGraph(attributePaths = {"zones", "tags"})
    Club findClubWithTagsAndZonesById(Long id);     // 동아리 지역과 태그정보 가져오기

    @EntityGraph(attributePaths = {"members", "managers"})
    Club findClubWithManagersAndMembersById(Long id);   //동아리 매니저와 회원정보 가져오기

}
