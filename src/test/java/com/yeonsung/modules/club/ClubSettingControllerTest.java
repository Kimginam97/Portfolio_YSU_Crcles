package com.yeonsung.modules.club;

import com.yeonsung.modules.account.WithAccount;
import com.yeonsung.modules.account.Account;
import com.yeonsung.modules.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
class ClubSettingControllerTest extends ClubControllerTest{

    @Autowired
    MockMvc mockMvc;
    @Autowired ClubService clubService;
    @Autowired ClubRepository clubRepository;
    @Autowired
    AccountRepository accountRepository;

    @Test
    @WithAccount("dudurian")
    void 동아리소개_폼수정권한_실패() throws Exception {
        Account helloClub = createAccount("helloClub");
        Club club = createClub("test-club", helloClub);

        mockMvc.perform(get("/club/" + club.getPath() + "/settings/description"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAccount("dudurian")
    void 동아리소개_폼수정조회_성공() throws Exception {
        Account dudurian = accountRepository.findByNickname("dudurian");
        Club club = createClub("test-club", dudurian);

        mockMvc.perform(get("/club/" + club.getPath() + "/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("club/settings/description"))
                .andExpect(model().attributeExists("clubDescriptionForm"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("club"));
    }

    @Test
    @WithAccount("dudurian")
    void 동아리_소개수정_성공() throws Exception {
        Account dudurian = accountRepository.findByNickname("dudurian");
        Club club = createClub("test-club", dudurian);

        String settingsDescriptionUrl = "/club/" + club.getPath() + "/settings/description";
        mockMvc.perform(post(settingsDescriptionUrl)
                .param("shortDescription", "short description")
                .param("fullDescription", "full description")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(settingsDescriptionUrl))
                .andExpect(flash().attributeExists("message"));
    }

    @Test
    @WithAccount("dudurian")
    void 동아리_소개수정_실패() throws Exception {
        Account dudurian = accountRepository.findByNickname("dudurian");
        Club club = createClub("test-club", dudurian);

        String settingsDescriptionUrl = "/club/" + club.getPath() + "/settings/description";
        mockMvc.perform(post(settingsDescriptionUrl)
                .param("shortDescription", "")
                .param("fullDescription", "full description")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("clubDescriptionForm"))
                .andExpect(model().attributeExists("club"))
                .andExpect(model().attributeExists("account"));
    }
}