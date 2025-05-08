package com.example.spot.refactor.study.presentation.dto.request;

import com.example.spot.refactor.common.presentation.validator.TextLength;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class StudyJoinRequestDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudyJoinDTO {

        @TextLength(min = 1, max = 255)
        private String introduction;
    }
}
