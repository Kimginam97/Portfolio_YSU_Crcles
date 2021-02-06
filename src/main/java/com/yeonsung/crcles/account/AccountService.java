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

    // 새로운 회원을 통해서 회원가입을 처리한다
    public void processSignUpByNewAccount(SignUpForm signUpForm){
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        sendSignUpByEmail(newAccount);
    }

    // 새로운 회원을 등록한다
    private Account saveNewAccount(@Valid SignUpForm signUpForm){
        Account newAccount = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .build();
        return accountRepository.save(newAccount);
    }

    // 이메일을 통해서 회원가입을 보낸다
    private void sendSignUpByEmail(Account newAccount) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(newAccount.getEmail());
        simpleMailMessage.setSubject("연성대학교 회원가입 이메일 인증");
        simpleMailMessage.setText("/Email-Token:" + newAccount.getEmailCheckToken());
        javaMailSender.send(simpleMailMessage);
    }

}
