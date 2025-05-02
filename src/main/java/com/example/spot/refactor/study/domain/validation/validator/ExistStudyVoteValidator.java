package com.example.spot.refactor.study.domain.validation.validator;

import com.example.spot.refactor.common.api.code.status.ErrorStatus;
import com.example.spot.refactor.study.domain.aggregate.studyvote.StudyVoteRepository;
import com.example.spot.refactor.study.domain.validation.annotation.ExistStudyVote;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistStudyVoteValidator implements ConstraintValidator<ExistStudyVote, Long> {

    private final StudyVoteRepository studyVoteRepository;

    @Override
    public void initialize(ExistStudyVote constraintAnnotation) {}

    @Override
    public boolean isValid(Long voteId, ConstraintValidatorContext context) {

        boolean isValid = false;
        ErrorStatus errorStatus;

        if (voteId == null) {
            errorStatus = ErrorStatus._STUDY_VOTE_NULL;
        } else if (!studyVoteRepository.existsById(voteId)) {
            errorStatus = ErrorStatus._STUDY_VOTE_NOT_FOUND;
        } else {
            errorStatus = ErrorStatus._STUDY_VOTE_NOT_FOUND; // ignore
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
