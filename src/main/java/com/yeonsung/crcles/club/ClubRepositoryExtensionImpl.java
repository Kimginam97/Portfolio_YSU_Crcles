package com.yeonsung.crcles.club;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.yeonsung.crcles.account.QAccount;
import com.yeonsung.crcles.club.QClub;
import com.yeonsung.crcles.tag.QTag;
import com.yeonsung.crcles.tag.Tag;
import com.yeonsung.crcles.zone.QZone;
import com.yeonsung.crcles.zone.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Set;

public class ClubRepositoryExtensionImpl extends QuerydslRepositorySupport implements ClubRepositoryExtension {

    public ClubRepositoryExtensionImpl() {
        super(Club.class);
    }

    @Override
    public Page<Club> findByKeyword(String keyword, Pageable pageable) {
        QClub club = QClub.club;
        JPQLQuery<Club> query = from(club).where(club.published.isTrue()
                .and(club.title.containsIgnoreCase(keyword))
                .or(club.tags.any().title.containsIgnoreCase(keyword))
                .or(club.zones.any().localNameOfCity.containsIgnoreCase(keyword)))
                .leftJoin(club.tags, QTag.tag).fetchJoin()
                .leftJoin(club.zones, QZone.zone).fetchJoin()
                .distinct();
        JPQLQuery<Club> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Club> fetchResults = pageableQuery.fetchResults();
        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }

    @Override
    public List<Club> findByAccount(Set<Tag> tags, Set<Zone> zones) {
        QClub club = QClub.club;
        JPQLQuery<Club> query = from(club).where(club.published.isTrue()
                .and(club.closed.isFalse())
                .and(club.tags.any().in(tags))
                .and(club.zones.any().in(zones)))
                .leftJoin(club.tags, QTag.tag).fetchJoin()
                .leftJoin(club.zones, QZone.zone).fetchJoin()
                .orderBy(club.publishedDateTime.desc())
                .distinct()
                .limit(6);
        return query.fetch();

    }
}
