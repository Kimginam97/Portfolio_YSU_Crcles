package com.yeonsung.crcles.event;

import com.yeonsung.crcles.account.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByEventAndAccount(Event event, Account account);

    Enrollment findByEventAndAccount(Event event, Account account);

    @EntityGraph("Enrollment.withEventAndClub")
    List<Enrollment> findByAccountAndAcceptedOrderByEnrolledAtDesc(Account account, boolean accepted);
}
