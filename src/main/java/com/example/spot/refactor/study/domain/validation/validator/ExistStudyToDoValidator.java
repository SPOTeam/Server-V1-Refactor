package com.example.spot.refactor.study.domain.validation.validator;

import com.example.spot.refactor.common.api.code.status.ErrorStatus;
import com.example.spot.refactor.study.domain.aggregate.studytodo.StudyToDoRepository;
import com.example.spot.refactor.study.domain.validation.annotation.ExistStudyToDo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistStudyToDoValidator implements ConstraintValidator<ExistStudyToDo, Long>{

    private final StudyToDoRepository studyToDoRepository;
    @Override
    public void initialize(ExistStudyToDo constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        boolean isValid = false;
        ErrorStatus errorStatus;

        if (value == null) {
            errorStatus = ErrorStatus._STUDY_TODO_NULL;
        } else if (!studyToDoRepository.existsById(value)) {
            errorStatus = ErrorStatus._STUDY_TODO_NOT_FOUND;
        } else {
            errorStatus = ErrorStatus._STUDY_TODO_NOT_FOUND; // ignore
            isValid = true;
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorStatus.getMessage())
                .addConstraintViolation();
        }

        return isValid;

    }
}
