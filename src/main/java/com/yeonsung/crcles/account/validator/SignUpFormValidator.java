package com.yeonsung.crcles.account.validator;

import com.yeonsung.crcles.account.AccountRepository;
import com.yeonsung.crcles.account.form.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object object, Errors errors) {

        SignUpForm signUpForm = (SignUpForm) object;
        // 스프링 jpa 를 이용해서 signUpForm 의 이메일 값이 있을 경우
        if (accountRepository.existsByEmail(signUpForm.getEmail())) {
            // 에러의 rejectValue 을 이용해서 잘못된값이다
            errors.rejectValue("email", "invalid.email", new Object[]{signUpForm.getEmail()}, "이미 사용중인 이메일입니다.");
        }

        if (accountRepository.existsByNickname(signUpForm.getNickname())) {
            errors.rejectValue("nickname", "invalid.nickname", new Object[]{signUpForm.getEmail()}, "이미 사용중인 닉네임입니다.");
        }
    }
}
