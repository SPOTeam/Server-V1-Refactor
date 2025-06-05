package com.example.spot.schedule.presentation.dto.response;

import com.example.spot.schedule.domain.Schedule;
import com.example.spot.schedule.domain.enums.SchedulePeriod;
import com.example.spot.study.domain.Study;
import lombok.*;
import org.springframework.data.domain.Page;

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

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SchedulePageDTO {
        private int totalPages;
        private long totalElements;
        private boolean first;
        private boolean last;
        private int size;
        private List<SchedulePreviewDTO> schedules;


        public SchedulePageDTO(Page<?> page, List<SchedulePreviewDTO> schedules, long totalElements){
            this.totalPages = totalElements == 0 ? 1 : (int) Math.ceil((double) totalElements / page.getSize());
            this.totalElements = totalElements;
            this.first = page.isFirst();
            this.last = page.isLast();
            this.size = page.getSize();
            this.schedules = schedules;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SchedulePreviewDTO {
        private LocalDateTime startedAt;
        private LocalDateTime finishedAt;
        private String title;
        private String location;
    }
}
