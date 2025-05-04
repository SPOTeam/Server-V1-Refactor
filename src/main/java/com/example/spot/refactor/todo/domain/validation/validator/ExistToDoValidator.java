package com.example.spot.refactor.todo.domain.validation.validator;

import com.example.spot.refactor.common.api.code.status.ErrorStatus;
import com.example.spot.refactor.todo.domain.ToDoRepository;
import com.example.spot.refactor.todo.domain.validation.annotation.ExistToDo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistToDoValidator implements ConstraintValidator<ExistToDo, Long>{

    private final ToDoRepository toDoRepository;
    @Override
    public void initialize(ExistToDo constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        boolean isValid = false;
        ErrorStatus errorStatus;

        if (value == null) {
            errorStatus = ErrorStatus._STUDY_TODO_NULL;
        } else if (!toDoRepository.existsById(value)) {
            errorStatus = ErrorStatus._STUDY_TODO_NOT_FOUND;
        } else {
            errorStatus = ErrorStatus._STUDY_TODO_NOT_FOUND; // ignore
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
