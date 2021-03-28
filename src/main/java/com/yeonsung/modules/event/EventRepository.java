package com.yeonsung.modules.event;

import com.yeonsung.modules.club.Club;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long> {

    @EntityGraph(value = "Event.withEnrollments", type = EntityGraph.EntityGraphType.LOAD)
    List<Event> findByClubOrderByStartDateTime(Club club); // 이벤트를 읽어올때 등록정보만 읽어오도록 설정 ex) 이벤트  -> 등록 -> 참여자의 관계에서 참여자까지는 가져오지 않는다

}