package com.yeonsung.crcles.account;

import com.yeonsung.crcles.account.form.NicknameForm;
import com.yeonsung.crcles.account.form.NotificationsForm;
import com.yeonsung.crcles.account.form.PasswordForm;
import com.yeonsung.crcles.account.form.ProfileForm;
import com.yeonsung.crcles.account.validator.NicknameFormValidator;
import com.yeonsung.crcles.account.validator.PasswordFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    // 프로필
    static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    static final String SETTINGS_PROFILE_URL = "/settings/profile";

    // 패스워드
    static final String SETTINGS_PASSWORD_VIEW_NAME = "settings/password";
    static final String SETTINGS_PASSWORD_URL = "/settings/password";

    // 알람
    static final String SETTINGS_NOTIFICATIONS_VIEW_NAME = "settings/notifications";
    static final String SETTINGS_NOTIFICATIONS_URL = "/settings/notifications";

    // 계정 닉네임
    static final String SETTINGS_ACCOUNT_VIEW_NAME = "settings/account";
    static final String SETTINGS_ACCOUNT_URL = "/" + SETTINGS_ACCOUNT_VIEW_NAME;

    // 태그
    static final String SETTINGS_TAGS_VIEW_NAME = "settings/tags";
    static final String SETTINGS_TAGS_URL = "/" + SETTINGS_TAGS_VIEW_NAME;


    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final NicknameFormValidator nicknameFormValidator;

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidator());
    }

    @InitBinder("nicknameForm")
    public void nicknameFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameFormValidator);
    }


    /*
    * 프로필 수정
    * */
    @GetMapping(SETTINGS_PROFILE_URL)
    public String updateProfileForm(@CurrentAccount Account account , Model model){
        model.addAttribute("account",account);

        // account 인스턴스를 Profile 매핑하여 Profile 객체 생성
        model.addAttribute("profileForm",modelMapper.map(account, ProfileForm.class));
        return SETTINGS_PROFILE_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PROFILE_URL)
    public String updateProfile(@CurrentAccount Account account, @Valid ProfileForm profileForm, Errors errors,
                                Model model, RedirectAttributes attributes){

        // 에러가 있을경우
       if (errors.hasErrors()){
           model.addAttribute("account",account);
           return SETTINGS_PROFILE_VIEW_NAME;
       }

       // 프로필 수정
       accountService.updateProfile(account,profileForm);

       // 알람메시지
       attributes.addFlashAttribute("message","프로필을 수정했습니다.");

       return "redirect:"+SETTINGS_PROFILE_URL;

    }

    /*
    * 패스워드 수정
    * */
    @GetMapping(SETTINGS_PASSWORD_URL)
    public String updatePasswordForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return SETTINGS_PASSWORD_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PASSWORD_URL)
    public String updatePassword(@CurrentAccount Account account, @Valid PasswordForm passwordForm, Errors errors,
                                 Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_PASSWORD_VIEW_NAME;
        }

        accountService.updatePassword(account, passwordForm.getNewPassword());
        attributes.addFlashAttribute("message", "패스워드를 변경했습니다.");
        return "redirect:" + SETTINGS_PASSWORD_URL;
    }


    /*
    * 알림 수정
    * */
    @GetMapping(SETTINGS_NOTIFICATIONS_URL)
    public String updateNotificationsForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute("notificationsForm",modelMapper.map(account,NotificationsForm.class));
        return SETTINGS_NOTIFICATIONS_VIEW_NAME;
    }

    @PostMapping(SETTINGS_NOTIFICATIONS_URL)
    public String updateNotifications(@CurrentAccount Account account, @Valid NotificationsForm notificationsForm, Errors errors,
                                      Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_NOTIFICATIONS_VIEW_NAME;
        }

        accountService.updateNotifications(account, notificationsForm);
        attributes.addFlashAttribute("message", "알림 설정을 변경했습니다.");
        return "redirect:" + SETTINGS_NOTIFICATIONS_URL;
    }

    /*
    * 계정 닉네임 수정
    * */
    @GetMapping(SETTINGS_ACCOUNT_URL)
    public String updateAccountForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));
        return SETTINGS_ACCOUNT_VIEW_NAME;
    }

    @PostMapping(SETTINGS_ACCOUNT_URL)
    public String updateAccount(@CurrentAccount Account account, @Valid NicknameForm nicknameForm, Errors errors,
                                Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_ACCOUNT_VIEW_NAME;
        }

        accountService.updateNickname(account, nicknameForm.getNickname());
        attributes.addFlashAttribute("message", "닉네임을 수정했습니다.");
        return "redirect:" + SETTINGS_ACCOUNT_URL;
    }

    /*
    * 태그 뷰
    * */
    @GetMapping(SETTINGS_TAGS_URL)
    public String updateTags(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        return SETTINGS_TAGS_VIEW_NAME;
    }

}
