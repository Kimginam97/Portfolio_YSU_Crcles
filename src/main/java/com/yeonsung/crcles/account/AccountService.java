package com.yeonsung.crcles.account;

import com.yeonsung.crcles.account.form.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;

    /*
    * 2021_02_05    2021_02_06
    * 회원가입기능    패스워드인코딩
    * */
    public Account saveNewAccount(@Valid SignUpForm signUpForm){
        Account newAccount = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .build();

        newAccount.generateEmailCheckToken();
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject("연성대학교 회원가입 이메일 인증");
        simpleMailMessage.setText("/Email-Token : " + newAccount.getEmailCheckToken());
        javaMailSender.send(simpleMailMessage);

        return accountRepository.save(newAccount);
    }

}
