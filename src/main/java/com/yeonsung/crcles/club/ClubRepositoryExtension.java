package com.yeonsung.crcles.club;

import com.yeonsung.crcles.tag.Tag;
import com.yeonsung.crcles.zone.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface ClubRepositoryExtension {

    Page<Club> findByKeyword(String keyword, Pageable pageable);

    List<Club> findByAccount(Set<Tag> tags, Set<Zone> zones);
}
