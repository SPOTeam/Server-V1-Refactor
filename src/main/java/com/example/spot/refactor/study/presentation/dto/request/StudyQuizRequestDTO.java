package com.example.spot.refactor.study.presentation.dto.request;

import com.example.spot.refactor.common.presentation.validator.TextLength;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class StudyQuizRequestDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuizDTO {

        private LocalDateTime createdAt;

        @TextLength(min = 1, max = 20)
        private String question;

        @TextLength(min = 1, max = 10)
        private String answer;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttendanceDTO {

        private LocalDateTime dateTime;

        @TextLength(min = 1, max = 10)
        private String answer;
    }
}
