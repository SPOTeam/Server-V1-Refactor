package com.example.spot.refactor.study.domain.validation.validator;

import com.example.spot.refactor.common.api.code.status.ErrorStatus;
import com.example.spot.refactor.study.domain.aggregate.studypost.LikedStudyCommentRepository;
import com.example.spot.refactor.study.domain.validation.annotation.ExistStudyLikedComment;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistStudyLikedCommentValidator implements ConstraintValidator<ExistStudyLikedComment, Long> {

    private final LikedStudyCommentRepository likedStudyCommentRepository;

    @Override
    public void initialize(ExistStudyLikedComment constraintAnnotation) {}

    @Override
    public boolean isValid(Long likedCommentId, ConstraintValidatorContext context) {

        boolean isValid = false;
        ErrorStatus errorStatus;

        if (likedCommentId == null) {
            errorStatus = ErrorStatus._STUDY_POST_COMMENT_REACTIOM_ID_NULL;
        } else if (!likedStudyCommentRepository.existsById(likedCommentId)) {
            errorStatus = ErrorStatus._STUDY_POST_COMMENT_REACTION_NOT_FOUND;
        } else {
            errorStatus = ErrorStatus._STUDY_POST_COMMENT_NOT_FOUND; // ignore
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
