package com.yeonsung.crcles.club.validator;

import com.yeonsung.crcles.club.ClubRepository;
import com.yeonsung.crcles.club.form.ClubForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class ClubFormValidator implements Validator {

    private final ClubRepository clubRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return ClubForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ClubForm clubForm = (ClubForm)o;

        if (clubRepository.existsByPath(clubForm.getPath())){
            errors.rejectValue("path", "wrong.path", "해당 동아리 경로값을 사용할 수 없습니다.");
        }

    }

}
