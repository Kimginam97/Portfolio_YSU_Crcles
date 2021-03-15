package com.yeonsung.crcles.account;

import com.yeonsung.crcles.account.form.NotificationsForm;
import com.yeonsung.crcles.account.form.ProfileForm;
import com.yeonsung.crcles.account.form.SignUpForm;
import com.yeonsung.crcles.config.AppProperties;
import com.yeonsung.crcles.mail.EmailMessage;
import com.yeonsung.crcles.mail.EmailService;
import com.yeonsung.crcles.tag.Tag;
import com.yeonsung.crcles.zone.Zone;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ModelMapper modelMapper;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    // 회원가입
    public Account processNewAccount(SignUpForm signUpForm) {

        // 회원 정보 저장
        Account newAccount = saveNewAccount(signUpForm);

        // 이메일 인증번호 전송
        sendSignUpConfirmEmail(newAccount);

        return newAccount;
    }

    // 회원 검증 이메일 보내기
    public void sendSignUpConfirmEmail(Account newAccount) {

        // HTML 내용
        Context context = new Context();
        context.setVariable("link", "/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());
        context.setVariable("nickname", newAccount.getNickname());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "스터디올래 서비스를 사용하려면 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(newAccount.getEmail())
                .subject("연성대학교 동아리 회원 가입 인증")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
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
        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        Account account = modelMapper.map(signUpForm, Account.class);
        account.generateEmailCheckToken();
        return accountRepository.save(account);
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

        Context context = new Context();
        context.setVariable("link", "/login-by-email?token=" + account.getEmailCheckToken() +
                "&email=" + account.getEmail());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName", "연성대 동아리 로그인하기");
        context.setVariable("message", "로그인 하려면 아래 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject("연성대학교 동아리 로그인 링크")
                .message(message)
                .build();
        emailService.sendEmail(emailMessage);
    }

    // 태그 추가하기
    public void addTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().add(tag));
    }


    // 태그 조회하기
    public Set<Tag> getTags(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getTags();
    }

    // 태그 삭제하기
    public void removeTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().remove(tag));
    }

    // 지역 조회
    public Set<Zone> getZones(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getZones();
    }

    // 지역 추가
    public void addZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getZones().add(zone));
    }

    // 지역 삭제
    public void removeZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getZones().remove(zone));
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
