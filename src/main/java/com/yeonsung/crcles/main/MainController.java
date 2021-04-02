package com.yeonsung.crcles.main;

import com.yeonsung.crcles.account.Account;
import com.yeonsung.crcles.account.AccountRepository;
import com.yeonsung.crcles.account.CurrentAccount;
import com.yeonsung.crcles.club.Club;
import com.yeonsung.crcles.club.ClubRepository;
import com.yeonsung.crcles.event.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ClubRepository clubRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AccountRepository accountRepository;

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {
        if (account != null) {
            Account accountLoaded = accountRepository.findAccountWithTagsAndZonesById(account.getId());
            model.addAttribute(accountLoaded);
            model.addAttribute("enrollmentList", enrollmentRepository.findByAccountAndAcceptedOrderByEnrolledAtDesc(accountLoaded, true));
            model.addAttribute("clubList", clubRepository.findByAccount(
                    accountLoaded.getTags(),
                    accountLoaded.getZones()));
            model.addAttribute("clubManagerOf",
                    clubRepository.findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(account, false));
            model.addAttribute("studyMemberOf",
                    clubRepository.findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(account, false));
            return "index-after-login";
        }

        model.addAttribute("clubList",clubRepository.findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(true,false));
        return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/search/club")
    public String searchClub(String keyword, Model model,
                             @PageableDefault(size = 9,sort = "publishedDateTime",direction = Sort.Direction.DESC)
                             Pageable pageable) {
        Page<Club> clubPage = clubRepository.findByKeyword(keyword,pageable);
        model.addAttribute("clubPage",clubPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty",
                pageable.getSort().toString().contains("publishedDateTime") ? "publishedDateTime" : "memberCount");
        return "search";
    }

}