package com.yeonsung.crcles.account;

import com.yeonsung.crcles.account.form.SignUpForm;
import com.yeonsung.crcles.account.validator.SignUpFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final SignUpFormValidator signUpFormValidator;
    private final AccountRepository accountRepository;

    // 커스텀한 signUpForm 검증 (이메일,닉네임 중복검사)
    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    // 회원가입
    @GetMapping("/sign-up")
    public String signUpFormView(Model model){
        model.addAttribute("signUpForm",new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpFormProcess(@Valid SignUpForm signUpForm, Errors errors){
        if(errors.hasErrors()){
            return "account/sign-up";
        }

        // 회원가입 처리기능
        accountService.processSignUpByNewAccount(signUpForm);
        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token,String email,Model model){

        Account account = accountRepository.findByEmail(email);
        String view = "account/checked-email";

        if (account == null){
            model.addAttribute("error","wrong.email");
            return view;
        }

        if(!account.isValidToken(token)){
            model.addAttribute("error","wrong.email");
            return view;
        }

        account.completeSignUpEmail();
        model.addAttribute("nickname",account.getNickname());
        return view;
    }

}
