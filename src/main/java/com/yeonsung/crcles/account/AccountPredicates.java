package com.yeonsung.crcles.account;

import com.querydsl.core.types.Predicate;
import com.yeonsung.crcles.account.QAccount;
import com.yeonsung.crcles.tag.Tag;
import com.yeonsung.crcles.zone.Zone;

import java.util.Set;

public class AccountPredicates {
    public static Predicate findByTagsAndZones(Set<Tag> tags, Set<Zone> zones) {
        QAccount account = QAccount.account;
        return account.zones.any().in(zones).and(account.tags.any().in(tags));
    }
}
