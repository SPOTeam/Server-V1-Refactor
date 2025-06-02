package com.example.spot.schedule.presentation.dto.request;

import com.example.spot.schedule.domain.enums.SchedulePeriod;
import com.example.spot.common.presentation.validator.TextLength;
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
        private SchedulePeriod schedulePeriod; // 반복 일정 여부
    }

}
