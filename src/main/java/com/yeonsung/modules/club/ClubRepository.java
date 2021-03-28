package com.yeonsung.modules.club;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ClubRepository extends JpaRepository<Club,Long> {

    boolean existsByPath(String path);  // path 존재하는지 여부

    @EntityGraph(value = "Club.withAll", type = EntityGraph.EntityGraphType.LOAD)
    Club findByPath(String path);   // 해당 정보의 path 가져올때 연관된 엔티티 그래프 값도 가져온다

    @EntityGraph(value = "Club.withTagsAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Club findClubWithTagsByPath(String path);    // 동아리 태그와 매니저 권한 가져오기

    @EntityGraph(value = "Club.withZonesAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Club findClubWithZonesByPath(String path);   // 동아리 지역과 매니저 권한만가져오기

    @EntityGraph(value = "Club.withManagers", type = EntityGraph.EntityGraphType.FETCH)
    Club findClubWithManagersByPath(String path);   // 동아리 매니저 권한만 가져오기

    @EntityGraph(value = "Club.withMembers", type = EntityGraph.EntityGraphType.FETCH)
    Club findStudyWithMembersByPath(String path);

    Club findClubOnlyByPath(String path);

}
