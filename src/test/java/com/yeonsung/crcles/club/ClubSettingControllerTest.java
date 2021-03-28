package com.yeonsung.crcles.club;

import com.yeonsung.crcles.account.AccountFactory;
import com.yeonsung.crcles.account.WithAccount;
import com.yeonsung.crcles.account.Account;
import com.yeonsung.crcles.account.AccountRepository;
import com.yeonsung.infra.MockMvcTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@MockMvcTest
class ClubSettingControllerTest{

    @Autowired
    MockMvc mockMvc;
    @Autowired ClubService clubService;
    @Autowired ClubRepository clubRepository;
    @Autowired AccountRepository accountRepository;
    @Autowired AccountFactory accountFactory;
    @Autowired ClubFactory clubFactory;


    @Test
    @WithAccount("dudurian")
    void 동아리소개_폼수정권한_실패() throws Exception {
        Account helloClub = accountFactory.createAccount("helloClub");
        Club club = clubFactory.createClub("test-club", helloClub);

        mockMvc.perform(get("/club/" + club.getPath() + "/settings/description"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAccount("dudurian")
    void 동아리소개_폼수정조회_성공() throws Exception {
        Account dudurian = accountRepository.findByNickname("dudurian");
        Club club = clubFactory.createClub("test-club", dudurian);

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
        Club club = clubFactory.createClub("test-club", dudurian);

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
        Club club = clubFactory.createClub("test-club", dudurian);

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