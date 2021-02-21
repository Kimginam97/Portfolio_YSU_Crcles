package com.yeonsung.crcles.account;

import com.yeonsung.crcles.account.form.SignUpForm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AccountService extends UserDetailsService {

    // 인증된 유저
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    // 새로운 회원의 회원가입을 처리한다
    public Account processSignUpByNewAccount(SignUpForm signUpForm);

    // 이메일을 통해서 회원가입을 보낸다
    public void sendSignUpConfirmEmail(Account newAccount);

    // 로그인 기능
    public void login(Account account);

}
