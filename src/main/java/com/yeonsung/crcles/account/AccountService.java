package com.yeonsung.crcles.account;

import com.yeonsung.crcles.account.form.NotificationsForm;
import com.yeonsung.crcles.account.form.ProfileForm;
import com.yeonsung.crcles.account.form.SignUpForm;
import com.yeonsung.crcles.tag.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final ModelMapper modelMapper;


    // 회원가입
    public Account processNewAccount(SignUpForm signUpForm) {

        // 회원 정보 저장
        Account newAccount = saveNewAccount(signUpForm);

        // 회원 이메일 토큰 생성
        newAccount.generateEmailCheckToken();

        // 이메일 인증번호 전송
        sendSignUpConfirmEmail(newAccount);

        return newAccount;
    }

    // 회원 검증 이메일 보내기
    public void sendSignUpConfirmEmail(Account newAccount) {

        // 메일 메시지 객체 생성
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        // 메일 받는 사람
        simpleMailMessage.setTo(newAccount.getEmail());

        // 메일 제목
        simpleMailMessage.setSubject("연성대학교 회원가입 이메일 인증");

        // 메일 내용
        simpleMailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() + "&email="+newAccount.getEmail());

        // 메일 보내기
        javaMailSender.send(simpleMailMessage);
    }

    // 로그인
    public void login(Account account) {

        // 인증된 회원 토큰 생성
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));

        // 토큰 설정
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    // 회원성공
    public void completeSignUp(Account account) {
        account.completeSignUpEmail();
        login(account);
    }

    // 회원 정보 저장
    public Account saveNewAccount(@Valid SignUpForm signUpForm){
        Account newAccount = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .build();
        return accountRepository.save(newAccount);
    }

    // 프로필 수정
    public void updateProfile(Account account, ProfileForm profileform) {

        // profile 인스터스를  account 매핑하여 account 객체 생성
        modelMapper.map(profileform, account);

        // account 저장
        accountRepository.save(account);
    }

    // 패스워드 수정
    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    // 알람 수정
    public void updateNotifications(Account account, NotificationsForm notificationsForm){

        modelMapper.map(notificationsForm,account);

        accountRepository.save(account);
    }

    // 닉네임 수정
    public void updateNickname(Account account, String nickname) {
        account.setNickname(nickname);
        accountRepository.save(account);
        login(account);
    }

    // 로그인 링크
    public void sendLoginLink(Account account) {
        account.generateEmailCheckToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(account.getEmail());
        mailMessage.setSubject("스터디올래, 로그인 링크");
        mailMessage.setText("/login-by-email?token=" + account.getEmailCheckToken() +
                "&email=" + account.getEmail());
        javaMailSender.send(mailMessage);
    }

    // 태그 추가하기
    public void addTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().add(tag));
    }


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {

        // 이메일로 회원정보 조회
        Account account = accountRepository.findByEmail(emailOrNickname);

        // 닉네임으로 조회
        if (account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }


        // 회원이 없을경우
        if (account==null){
            throw new UsernameNotFoundException(emailOrNickname);
        }

        // 회원이 있을경우
        return new UserAccount(account);
    }

}
