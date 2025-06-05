package com.example.spot.study.presentation.dto.response;

import com.example.spot.member.domain.Member;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.member.domain.enums.Gender;
import com.example.spot.study.domain.enums.ThemeType;
import com.example.spot.study.domain.Study;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
public class StudyInfoResponseDTO {

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

}
