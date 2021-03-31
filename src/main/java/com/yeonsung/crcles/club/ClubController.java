package com.yeonsung.crcles.club;

import com.yeonsung.crcles.account.Account;
import com.yeonsung.crcles.account.CurrentAccount;
import com.yeonsung.crcles.club.form.ClubForm;
import com.yeonsung.crcles.club.validator.ClubFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;
    private final ModelMapper modelMapper;
    private final ClubFormValidator clubFormValidator;
    private final ClubRepository clubRepository;

    @InitBinder("clubForm")
    public void clubFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(clubFormValidator);
    }


    /*
    * 동아리 생성
    * */
    @GetMapping("/new-club")
    public String newClubForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new ClubForm());
        return "club/form";
    }

    @PostMapping("/new-club")
    public String newClubSubmit(@CurrentAccount Account account, @Valid ClubForm clubForm, Errors errors,Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("account",account);
            return "club/form";
        }

        Club newClub = clubService.createNewClub(modelMapper.map(clubForm, Club.class), account);
        return "redirect:/club/" + URLEncoder.encode(newClub.getPath(), StandardCharsets.UTF_8);
    }

    /*
    * 동아리 조회
    * 동아리학생 조회
    * */

    @GetMapping("/club/{path}")
    public String viewClub(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClub(path);
        model.addAttribute("account",account);
        model.addAttribute("club",club);
        return "club/view";
    }

    @GetMapping("/club/{path}/members")
    public String viewClubMembers(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClub(path);
        model.addAttribute("account",account);
        model.addAttribute("club",club);
        return "club/members";
    }

    /*
    * 동아리 참가
    * 동아리 탈퇴
    * TODO : POST 요청으로 바꾸기
    * */

    @GetMapping("/club/{path}/join")
    public String joinClub(@CurrentAccount Account account, @PathVariable String path) {
        Club club = clubRepository.findClubWithMembersByPath(path);
        clubService.addMember(club, account);
        return "redirect:/club/" + club.getEncodedPath() + "/members";
    }

    @GetMapping("/club/{path}/leave")
    public String leaveClub(@CurrentAccount Account account, @PathVariable String path) {
        Club club = clubRepository.findClubWithMembersByPath(path);
        clubService.removeMember(club, account);
        return "redirect:/club/" + club.getEncodedPath() + "/members";
    }


}
