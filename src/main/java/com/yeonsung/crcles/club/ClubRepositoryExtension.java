package com.yeonsung.crcles.club;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ClubRepositoryExtension {

    Page<Club> findByKeyword(String keyword, Pageable pageable);
}
