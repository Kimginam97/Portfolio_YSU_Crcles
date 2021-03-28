package com.yeonsung.crcles.club;

import com.yeonsung.crcles.WithAccount;
import com.yeonsung.crcles.account.Account;
import com.yeonsung.crcles.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
public class ClubControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired protected ClubService clubService;
    @Autowired protected ClubRepository clubRepository;
    @Autowired
    protected AccountRepository accountRepository;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    @WithAccount("dudurian")
    @DisplayName("스터디 개설 폼 조회")
    void 동아리_생성_뷰() throws Exception {
        mockMvc.perform(get("/new-club"))
                .andExpect(status().isOk())
                .andExpect(view().name("club/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("clubForm"));
    }

    @Test
    @WithAccount("dudurian")
    void 동아리_생성_성공() throws Exception {
        mockMvc.perform(post("/new-club")
                .param("path", "test-path")
                .param("title", "study title")
                .param("shortDescription", "short description of a study")
                .param("fullDescription", "full description of a study")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/test-path"));

        Club club = clubRepository.findByPath("test-path");
        assertNotNull(club);
        Account account = accountRepository.findByNickname("dudurian");
        assertTrue(club.getManagers().contains(account));
    }

    @Test
    @WithAccount("dudurian")
    void 동아리_생성_실패() throws Exception {
        mockMvc.perform(post("/new-club")
                .param("path", "wrong path")
                .param("title", "study title")
                .param("shortDescription", "short description of a study")
                .param("fullDescription", "full description of a study")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("club/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("clubForm"))
                .andExpect(model().attributeExists("account"));

        Club club = clubRepository.findByPath("test-path");
        assertNull(club);
    }

    @Test
    @WithAccount("dudurian")
    void 동아리학생_조회_성공() throws Exception {
        Club club = new Club();
        club.setPath("test-path");
        club.setTitle("test study");
        club.setShortDescription("short description");
        club.setFullDescription("<p>full description</p>");

        Account dudurian = accountRepository.findByNickname("dudurian");
        clubService.createNewClub(club, dudurian);

        mockMvc.perform(get("/club/test-path"))
                .andExpect(view().name("club/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("club"));
    }

    @Test
    @WithAccount("dudurian")
    void 동아리_가입_성공() throws Exception {
        Account helloClub = createAccount("helloClub");

        Club club = createClub("test-club", helloClub);

        mockMvc.perform(get("/club/" + club.getPath() + "/join"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getPath() + "/members"));

        Account dudurian = accountRepository.findByNickname("dudurian");
        assertTrue(club.getMembers().contains(dudurian));
    }

    @Test
    @WithAccount("dudurian")
    void 동아리_탈퇴_성공() throws Exception {
        Account helloClub = createAccount("helloClub");
        Club club = createClub("test-club", helloClub);

        Account dudurian = accountRepository.findByNickname("dudurian");
        clubService.addMember(club, helloClub);

        mockMvc.perform(get("/club/" + club.getPath() + "/leave"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getPath() + "/members"));

        assertFalse(club.getMembers().contains(dudurian));
    }

    protected Club createClub(String path, Account manager) {
        Club club = new Club();
        club.setPath(path);
        clubService.createNewClub(club, manager);
        return club;
    }

    protected Account createAccount(String nickname) {
        Account helloClub = new Account();
        helloClub.setNickname(nickname);
        helloClub.setEmail(nickname + "@email.com");
        accountRepository.save(helloClub);
        return helloClub;
    }
}