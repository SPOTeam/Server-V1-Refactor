package com.example.spot.refactor.study.domain.validation.validator;

import com.example.spot.refactor.common.api.code.status.ErrorStatus;
import com.example.spot.refactor.study.domain.enums.ThemeType;
import com.example.spot.refactor.study.domain.aggregate.studytheme.ThemeRepository;
import com.example.spot.refactor.study.domain.validation.annotation.ExistTheme;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistThemeValidator implements ConstraintValidator<ExistTheme, String> {

    private final ThemeRepository themeRepository;
    @Override
    public void initialize(ExistTheme constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isValid;
        ErrorStatus errorStatus;

        if (value == null) {
            errorStatus = ErrorStatus._STUDY_THEME_IS_NULL;
            isValid = false;
        } else {
            try {
                ThemeType themeType = ThemeType.valueOf(value.toUpperCase());
                isValid = themeRepository.existsByThemeType(themeType);
                errorStatus = isValid ? null : ErrorStatus._STUDY_THEME_NOT_FOUND;
            } catch (IllegalArgumentException e) {
                isValid = false;
                errorStatus = ErrorStatus._STUDY_THEME_NOT_FOUND;
            }
        }

        if (!isValid) {
            Objects.requireNonNull(context).disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorStatus.toString())
                .addConstraintViolation();
        }

        return isValid;
    }


}
