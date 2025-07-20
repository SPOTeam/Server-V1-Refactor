package com.example.spot.story.domain.validation.validator;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.story.infrastructure.repository.LikedStoryCommentRepository;
import com.example.spot.story.domain.validation.annotation.ExistLikedStoryComment;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistLikedStoryCommentValidator implements ConstraintValidator<ExistLikedStoryComment, Long> {

    private final LikedStoryCommentRepository likedStoryCommentRepository;

    @Override
    public void initialize(ExistLikedStoryComment constraintAnnotation) {}

    @Override
    public boolean isValid(Long likedCommentId, ConstraintValidatorContext context) {

        boolean isValid = false;
        ErrorStatus errorStatus;

        if (likedCommentId == null) {
            errorStatus = ErrorStatus._STUDY_POST_COMMENT_REACTIOM_ID_NULL;
        } else if (!likedStoryCommentRepository.existsById(likedCommentId)) {
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
