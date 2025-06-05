package com.example.spot.study.presentation.dto.response;

import com.example.spot.member.domain.association.PreferredStudy;
import com.example.spot.member.domain.enums.Status;
import com.example.spot.study.domain.Study;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.study.domain.enums.StudyLikeStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
public class StudyResponseDTO {

    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TitleDTO {

        private final Long studyId;
        private final String title;

        public static TitleDTO toDTO(Study study) {
            return TitleDTO.builder()
                    .studyId(study.getId())
                    .title(study.getTitle())
                    .build();
        }
    }

    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RegisterDTO {

        private final Long studyId;
        private final String title;

        public static RegisterDTO toDTO(Study study) {
            return RegisterDTO.builder()
                    .studyId(study.getId())
                    .title(study.getTitle())
                    .build();
        }
    }

    @Getter
    @Builder(access = AccessLevel.PRIVATE)

    public static class TerminationDTO {

        private final Long studyId;
        private final String studyName;
        private final Status status;

        public static TerminationDTO toDTO(Study study) {
            return TerminationDTO.builder()
                    .studyId(study.getId())
                    .studyName(study.getTitle())
                    .status(study.getStatus())
                    .build();
        }
    }

    @Getter
    @Builder
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LikeDTO {
        private final String studyTitle;
        private final LocalDateTime createdAt;
        private final StudyLikeStatus status;

        public LikeDTO(PreferredStudy preferredStudy){
            this.studyTitle = preferredStudy.getStudy().getTitle();
            this.createdAt = preferredStudy.getCreatedAt();
            this.status = preferredStudy.getStudyLikeStatus();
        }
    }
}
