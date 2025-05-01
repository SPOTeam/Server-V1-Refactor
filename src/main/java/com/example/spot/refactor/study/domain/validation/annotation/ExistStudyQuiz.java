package com.example.spot.refactor.study.domain.validation.annotation;

import com.example.spot.refactor.study.domain.validation.validator.ExistStudyQuizValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExistStudyQuizValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistStudyQuiz {
    String message() default "해당하는 퀴즈가 존재하지 않습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
