package com.example.spot.refactor.study.domain.validation.validator;

import com.example.spot.refactor.common.api.code.status.ErrorStatus;
import com.example.spot.refactor.study.domain.aggregate.studyschedule.StudyQuizRepository;
import com.example.spot.refactor.study.domain.validation.annotation.ExistQuiz;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistQuizValidator implements ConstraintValidator<ExistQuiz, Long> {

    private final StudyQuizRepository studyQuizRepository;

    @Override
    public void initialize(ExistQuiz constraintAnnotation) {}

    @Override
    public boolean isValid(Long quizId, ConstraintValidatorContext context) {

        boolean isValid = false;
        ErrorStatus errorStatus;

        if (quizId == null) {
            errorStatus = ErrorStatus._STUDY_QUIZ_ID_NULL;
        } else if (!studyQuizRepository.existsById(quizId)) {
            errorStatus = ErrorStatus._STUDY_QUIZ_NOT_FOUND;
        } else {
            errorStatus = ErrorStatus._STUDY_QUIZ_NOT_FOUND; // ignore
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
