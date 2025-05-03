package com.example.spot.refactor.schedule.domain.validation.annotation;

import com.example.spot.refactor.schedule.domain.validation.validator.ExistStudyScheduleValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExistStudyScheduleValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistStudySchedule {
    String message() default "해당하는 일정이 존재하지 않습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
