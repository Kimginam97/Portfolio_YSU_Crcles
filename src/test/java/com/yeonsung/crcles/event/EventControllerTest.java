package com.yeonsung.crcles.event;

import com.yeonsung.crcles.WithAccount;
import com.yeonsung.crcles.account.Account;
import com.yeonsung.crcles.club.Club;
import com.yeonsung.crcles.club.ClubControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EventControllerTest extends ClubControllerTest {

    @Autowired
    EventService eventService;
    @Autowired EnrollmentRepository enrollmentRepository;

    @Test
    @WithAccount("dudurian")
    void 선착순모임_참가신청_자동수락() throws Exception {
        Account helloClub = createAccount("helloClub");
        Club club = createClub("test-club", helloClub);
        Event event = createEvent("test-event", EventType.FCFS, 2, club, helloClub);

        mockMvc.perform(post("/club/" + club.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getPath() + "/events/" + event.getId()));

        Account dudurian = accountRepository.findByNickname("dudurian");
        isAccepted(dudurian, event);
    }

    @Test
    @WithAccount("dudurian")
    void 선착순모임_참가신청_대기중() throws Exception {
        Account helloClub = createAccount("helloClub");
        Club club = createClub("test-club", helloClub);
        Event event = createEvent("test-event", EventType.FCFS, 2, club, helloClub);

        Account may = createAccount("may");
        Account june = createAccount("june");
        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, june);

        mockMvc.perform(post("/club/" + club.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getPath() + "/events/" + event.getId()));

        Account dudurian = accountRepository.findByNickname("dudurian");
        isNotAccepted(dudurian, event);
    }

    @Test
    @WithAccount("dudurian")
    void 참가신청확장자_취소_다음대기자_신청확인() throws Exception {
        Account dudurian = accountRepository.findByNickname("dudurian");
        Account helloClub = createAccount("helloClub");
        Account may = createAccount("may");
        Club club = createClub("test-club", helloClub);
        Event event = createEvent("test-event", EventType.FCFS, 2, club, helloClub);

        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, dudurian);
        eventService.newEnrollment(event, helloClub);

        isAccepted(may, event);
        isAccepted(dudurian, event);
        isNotAccepted(helloClub, event);

        mockMvc.perform(post("/club/" + club.getPath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getPath() + "/events/" + event.getId()));

        isAccepted(may, event);
        isAccepted(helloClub, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, dudurian));
    }

    @Test
    @WithAccount("dudurian")
    void 대기자참가_취소_기존참가자_유지() throws Exception {
        Account dudurian = accountRepository.findByNickname("dudurian");
        Account helloClub = createAccount("helloClub");
        Account may = createAccount("may");
        Club club = createClub("test-club", helloClub);
        Event event = createEvent("test-event", EventType.FCFS, 2, club, helloClub);

        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, helloClub);
        eventService.newEnrollment(event, dudurian);

        isAccepted(may, event);
        isAccepted(helloClub, event);
        isNotAccepted(dudurian, event);

        mockMvc.perform(post("/club/" + club.getPath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getPath() + "/events/" + event.getId()));

        isAccepted(may, event);
        isAccepted(helloClub, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, dudurian));
    }

    private void isNotAccepted(Account whiteship, Event event) {
        assertFalse(enrollmentRepository.findByEventAndAccount(event, whiteship).isAccepted());
    }

    private void isAccepted(Account account, Event event) {
        assertTrue(enrollmentRepository.findByEventAndAccount(event, account).isAccepted());
    }

    @Test
    @WithAccount("dudurian")
    void 관리자확인모임_참가신청_대기() throws Exception {
        Account helloClub = createAccount("helloClub");
        Club club = createClub("test-club", helloClub);
        Event event = createEvent("test-event", EventType.CONFIRMATIVE, 2, club, helloClub);

        mockMvc.perform(post("/club/" + club.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getPath() + "/events/" + event.getId()));

        Account dudurian = accountRepository.findByNickname("dudurian");
        isNotAccepted(dudurian, event);
    }

    private Event createEvent(String eventTitle, EventType eventType, int limit, Club club, Account account) {
        Event event = new Event();
        event.setEventType(eventType);
        event.setLimitOfEnrollments(limit);
        event.setTitle(eventTitle);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setEndEnrollmentDateTime(LocalDateTime.now().plusDays(1));
        event.setStartDateTime(LocalDateTime.now().plusDays(1).plusHours(5));
        event.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(7));
        return eventService.createEvent(event, club, account);
    }

}