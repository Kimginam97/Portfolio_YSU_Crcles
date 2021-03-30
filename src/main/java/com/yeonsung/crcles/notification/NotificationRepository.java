package com.yeonsung.crcles.notification;

import com.yeonsung.crcles.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification,Long> {

    // 읽은 메시지
    long countByAccountAndChecked(Account account, boolean checked);

    // 읽지않은 메시지
    @Transactional
    List<Notification> findByAccountAndCheckedOrderByCreatedDateTimeDesc(Account account, boolean checked);

    // 알림삭제
    @Transactional
    void deleteByAccountAndChecked(Account account, boolean checked);
}
