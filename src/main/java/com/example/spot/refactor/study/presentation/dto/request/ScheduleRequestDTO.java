package com.example.spot.refactor.study.presentation.dto.request;

import com.example.spot.refactor.schedule.domain.enums.StudySchedulePeriod;
import com.example.spot.legacy.validation.annotation.TextLength;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter

public class ScheduleRequestDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleDTO {

        @TextLength(min = 1, max = 20)
        private String title;

        @TextLength(min = 1, max = 20)
        private String location;

        private LocalDateTime startedAt;
        private LocalDateTime finishedAt;
        private Boolean isAllDay; // 종일 진행 여부
        private StudySchedulePeriod studySchedulePeriod; // 반복 일정 여부
    }

}
