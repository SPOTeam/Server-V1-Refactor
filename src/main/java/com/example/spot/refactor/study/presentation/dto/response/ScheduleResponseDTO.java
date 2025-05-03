package com.example.spot.refactor.study.presentation.dto.response;

import com.example.spot.refactor.schedule.domain.Schedule;
import com.example.spot.refactor.schedule.domain.enums.SchedulePeriod;
import com.example.spot.refactor.study.domain.Study;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ScheduleResponseDTO {

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class ScheduleDTO {

        private final Long studyId;
        private final Long scheduleId;
        private final String title;

        private final LocalDateTime startedAt;
        private final LocalDateTime finishedAt;

        public static ScheduleDTO toDTO(Schedule schedule) {
            return ScheduleDTO.builder()
                    .studyId(schedule.getStudy().getId())
                    .scheduleId(schedule.getId())
                    .title(schedule.getTitle())
                    .startedAt(schedule.getStartedAt())
                    .finishedAt(schedule.getFinishedAt())
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class MonthlyScheduleListDTO {

        private final Long studyId;
        private final List<MonthlyScheduleDTO> scheduleList;

        public static MonthlyScheduleListDTO toDTO(Study study, List<MonthlyScheduleDTO> scheduleList) {
            return MonthlyScheduleListDTO.builder()
                    .studyId(study.getId())
                    .scheduleList(scheduleList)
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class MonthlyScheduleDTO {

        private final Long scheduleId;
        private final String title;
        private final String location;
        private final LocalDateTime startedAt;
        private final LocalDateTime finishedAt;
        private final Boolean isAllDay;
        private final SchedulePeriod schedulePeriod;

        public static MonthlyScheduleDTO toDTO(
                Schedule schedule, boolean isStudyMember) {
            return MonthlyScheduleDTO.builder()
                    .scheduleId(schedule.getId())
                    .title(schedule.getTitle())
                    .location(isStudyMember ? schedule.getLocation() : "공개되지 않습니다.")
                    .startedAt(schedule.getStartedAt())
                    .finishedAt(schedule.getFinishedAt())
                    .isAllDay(schedule.getIsAllDay())
                    .schedulePeriod(schedule.getSchedulePeriod())
                    .build();
        }

        public static MonthlyScheduleDTO toDTOWithDate(
                Schedule schedule, LocalDateTime startedAt, LocalDateTime finishedAt, boolean isStudyMember) {
            return MonthlyScheduleDTO.builder()
                    .scheduleId(schedule.getId())
                    .title(schedule.getTitle())
                    .location(isStudyMember ? schedule.getLocation() : "공개되지 않습니다.")
                    .startedAt(startedAt)
                    .finishedAt(finishedAt)
                    .isAllDay(schedule.getIsAllDay())
                    .schedulePeriod(schedule.getSchedulePeriod())
                    .build();
        }
    }
}
