package com.example.spot.refactor.story.domain.validation.validator;

import com.example.spot.refactor.common.api.code.status.ErrorStatus;
import com.example.spot.refactor.story.domain.repository.StoryCommentRepository;
import com.example.spot.refactor.story.domain.validation.annotation.ExistStoryComment;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistStoryCommentValidator implements ConstraintValidator<ExistStoryComment, Long> {

    private final StoryCommentRepository storyCommentRepository;

    @Override
    public void initialize(ExistStoryComment constraintAnnotation) {}

    @Override
    public boolean isValid(Long commentId, ConstraintValidatorContext context) {
        
        boolean isValid = false;
        ErrorStatus errorStatus;

        if (commentId == null) {
            errorStatus = ErrorStatus._STUDY_POST_COMMENT_NULL;
        } else if (!storyCommentRepository.existsById(commentId)) {
            errorStatus = ErrorStatus._STUDY_POST_COMMENT_NOT_FOUND;
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
