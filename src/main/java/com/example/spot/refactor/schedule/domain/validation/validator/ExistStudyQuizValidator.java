package com.example.spot.refactor.schedule.domain.validation.validator;

import com.example.spot.refactor.common.api.code.status.ErrorStatus;
import com.example.spot.refactor.schedule.domain.repository.StudyQuizRepository;
import com.example.spot.refactor.schedule.domain.validation.annotation.ExistStudyQuiz;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistStudyQuizValidator implements ConstraintValidator<ExistStudyQuiz, Long> {

    private final StudyQuizRepository studyQuizRepository;

    @Override
    public void initialize(ExistStudyQuiz constraintAnnotation) {}

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
