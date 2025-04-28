package com.example.spot.legacy.validation.validator;

import com.example.spot.legacy.api.code.status.ErrorStatus;
import com.example.spot.legacy.repository.PostRepository;
import com.example.spot.legacy.validation.annotation.ExistPost;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistPostValidator implements ConstraintValidator<ExistPost, Long> {

    private final PostRepository postRepository;

    @Override
    public void initialize(ExistPost constraintAnnotation) {}

    @Override
    public boolean isValid(Long postId, ConstraintValidatorContext context) {

        boolean isValid = false;
        ErrorStatus errorStatus;

        if (postId == null) {
            errorStatus = ErrorStatus._POST_ID_NULL;
        } else if (!postRepository.existsById(postId)) {
            errorStatus = ErrorStatus._POST_NOT_FOUND;
        } else {
            errorStatus = ErrorStatus._POST_NOT_FOUND; // ignore
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
