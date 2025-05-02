package com.example.spot.legacy.validation.validator;

import com.example.spot.refactor.common.api.code.status.ErrorStatus;
import com.example.spot.refactor.study.domain.aggregate.studyregion.RegionRepository;
import com.example.spot.legacy.validation.annotation.ExistRegion;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistRegionValidator implements ConstraintValidator<ExistRegion, String> {
    private final RegionRepository regionRepository;

    @Override
    public void initialize(ExistRegion constraintAnnotation) {
        // Initialization logic if needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isValid;
        ErrorStatus errorStatus;

        // null or empty values are considered invalid
        if (value == null || value.isEmpty()) {
            errorStatus = ErrorStatus._STUDY_REGION_IS_NULL;
            isValid = false;
        } else {
            errorStatus = ErrorStatus._STUDY_REGION_NOT_FOUND;
            isValid = regionRepository.existsByCode(value);
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorStatus.getMessage())
                .addConstraintViolation();
        }

        return isValid;
    }
}


