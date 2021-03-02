package com.yeonsung.crcles.account;

import com.yeonsung.crcles.account.form.Profile;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    static final String SETTINGS_PROFILE_URL = "/settings/profile";

    private final AccountService accountService;
    private final ModelMapper modelMapper;


    @GetMapping(SETTINGS_PROFILE_URL)
    public String updateProfileForm(@CurrentAccount Account account , Model model){
        model.addAttribute("account",account);

        // account 인스턴스를 Profile 매핑하여 Profile 객체 생성
        model.addAttribute("profile",modelMapper.map(account,Profile.class));
        return SETTINGS_PROFILE_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PROFILE_URL)
    public String updateProfile(@CurrentAccount Account account, @Valid Profile profile,Errors errors,
                                Model model, RedirectAttributes attributes){

        // 에러가 있을경우
       if (errors.hasErrors()){
           model.addAttribute("account",account);
           return SETTINGS_PROFILE_VIEW_NAME;
       }

       // 프로필 수정
       accountService.updateProfile(account,profile);

       // 알람메시지
       attributes.addFlashAttribute("message","프로필을 수정했습니다.");

       return "redirect:"+SETTINGS_PROFILE_URL;

    }

}
