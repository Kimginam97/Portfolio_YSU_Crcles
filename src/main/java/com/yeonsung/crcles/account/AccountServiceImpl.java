package com.yeonsung.crcles.account;

import com.yeonsung.crcles.account.form.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountServiceImpl implements UserDetailsService , AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email);
        if (account==null){
            throw new UsernameNotFoundException(email);
        }
        return new UserAccount(account);
    }

    @Override
    @Transactional
    public Account processSignUpByNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    @Override
    public void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(newAccount.getEmail());
        simpleMailMessage.setSubject("연성대학교 회원가입 이메일 인증");
        simpleMailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() + "&email="+newAccount.getEmail());
        javaMailSender.send(simpleMailMessage);
    }

    @Override
    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContextHolder.getContext().setAuthentication(token);
    }

    public Account saveNewAccount(@Valid SignUpForm signUpForm){
        Account newAccount = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .build();
        return accountRepository.save(newAccount);
    }

}
