package com.example.spot.refactor.study.presentation.dto.response;

import com.example.spot.refactor.schedule.domain.StudySchedule;
import com.example.spot.refactor.schedule.domain.enums.StudySchedulePeriod;
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

        public static ScheduleDTO toDTO(StudySchedule studySchedule) {
            return ScheduleDTO.builder()
                    .studyId(studySchedule.getStudy().getId())
                    .scheduleId(studySchedule.getId())
                    .title(studySchedule.getTitle())
                    .startedAt(studySchedule.getStartedAt())
                    .finishedAt(studySchedule.getFinishedAt())
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
        private final StudySchedulePeriod studySchedulePeriod;

        public static MonthlyScheduleDTO toDTO(
                StudySchedule studySchedule, boolean isStudyMember) {
            return MonthlyScheduleDTO.builder()
                    .scheduleId(studySchedule.getId())
                    .title(studySchedule.getTitle())
                    .location(isStudyMember ? studySchedule.getLocation() : "공개되지 않습니다.")
                    .startedAt(studySchedule.getStartedAt())
                    .finishedAt(studySchedule.getFinishedAt())
                    .isAllDay(studySchedule.getIsAllDay())
                    .studySchedulePeriod(studySchedule.getStudySchedulePeriod())
                    .build();
        }

        public static MonthlyScheduleDTO toDTOWithDate(
                StudySchedule studySchedule, LocalDateTime startedAt, LocalDateTime finishedAt, boolean isStudyMember) {
            return MonthlyScheduleDTO.builder()
                    .scheduleId(studySchedule.getId())
                    .title(studySchedule.getTitle())
                    .location(isStudyMember ? studySchedule.getLocation() : "공개되지 않습니다.")
                    .startedAt(startedAt)
                    .finishedAt(finishedAt)
                    .isAllDay(studySchedule.getIsAllDay())
                    .studySchedulePeriod(studySchedule.getStudySchedulePeriod())
                    .build();
        }
    }
}
