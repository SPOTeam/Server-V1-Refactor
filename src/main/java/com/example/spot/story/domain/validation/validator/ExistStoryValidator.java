package com.example.spot.story.domain.validation.validator;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.story.infrastructure.repository.StoryRepository;
import com.example.spot.story.domain.validation.annotation.ExistStory;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistStoryValidator implements ConstraintValidator<ExistStory, Long> {

    private final StoryRepository storyRepository;

    @Override
    public void initialize(ExistStory constraintAnnotation) {}

    @Override
    public boolean isValid(Long studyPostId, ConstraintValidatorContext context) {

        boolean isValid = false;
        ErrorStatus errorStatus;

        if (studyPostId == null) {
            errorStatus = ErrorStatus._STUDY_POST_NULL;
        } else if (!storyRepository.existsById(studyPostId)) {
            errorStatus = ErrorStatus._STUDY_POST_NOT_FOUND;
        } else {
            errorStatus = ErrorStatus._STUDY_POST_NOT_FOUND; // ignore
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
