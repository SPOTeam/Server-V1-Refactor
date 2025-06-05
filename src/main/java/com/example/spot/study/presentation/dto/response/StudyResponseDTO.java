package com.example.spot.study.presentation.dto.response;

import com.example.spot.member.domain.Member;
import com.example.spot.member.domain.association.PreferredStudy;
import com.example.spot.member.domain.enums.Gender;
import com.example.spot.member.domain.enums.Status;
import com.example.spot.study.domain.Study;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.study.domain.enums.StudyLikeStatus;
import com.example.spot.study.domain.enums.ThemeType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class StudyResponseDTO {

    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class StudyInfoDTO {

        private final Long studyId;
        private final String studyName;
        private final StudyMemberResponseDTO.HostNameDTO studyOwner;
        private final Long hitNum;
        private final Integer heartCount;
        private final Integer memberCount;
        private final Long maxPeople;
        private final Gender gender;
        private final Integer minAge;
        private final Integer maxAge;
        private final Integer fee;
        private final Boolean isOnline;
        private final String profileImage;
        private final List<ThemeType> themes;
        private final List<String> regions;
        private final String goal;
        private final String introduction;

        public static StudyInfoDTO toDTO(Study study, Member owner) {
            return StudyInfoDTO.builder()
                    .studyId(study.getId())
                    .studyName(study.getTitle())
                    .studyOwner(StudyMemberResponseDTO.HostNameDTO.toDTO(owner))
                    .hitNum(study.getHitNum())
                    .heartCount(study.getHeartCount())
                    .memberCount(
                            study.getMemberStudies().stream()
                                    .filter(memberStudy -> memberStudy.getStatus().equals(StudyApplicationStatus.APPROVED))
                                    .toList()
                                    .size())
                    .maxPeople(study.getMaxPeople())
                    .gender(study.getGender())
                    .minAge(study.getMinAge())
                    .maxAge(study.getMaxAge())
                    .fee(study.getFee())
                    .isOnline(study.getIsOnline())
                    .profileImage(study.getProfileImage())
                    .themes(study.getStudyThemes().stream()
                            .map(memberStudy -> { return memberStudy.getTheme().getThemeType();})
                            .toList())
                    .regions(study.getRegionStudies().stream()
                            .map(memberStudy -> { return memberStudy.getRegion().getCode();})
                            .toList())
                    .goal(study.getGoal())
                    .introduction(study.getIntroduction())
                    .build();
        }
    }

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
