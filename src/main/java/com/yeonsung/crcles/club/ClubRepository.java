package com.yeonsung.crcles.club;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ClubRepository extends JpaRepository<Club,Long> {

    boolean existsByPath(String path);  // path 존재하는지 여부

    @EntityGraph(value = "Club.withAll", type = EntityGraph.EntityGraphType.LOAD)
    Club findByPath(String path);   // 해당 정보의 path 가져올때 연관된 엔티티 그래프 값도 가져온다

    @EntityGraph(value = "Club.withTagsAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Club findAccountWithTagsByPath(String path);    // 동아리 태그와 권한만 가져오기

    @EntityGraph(value = "Club.withZonesAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Club findAccountWithZonesByPath(String path);   // 동아리지역과 매니저 권한만 가져오기
}
