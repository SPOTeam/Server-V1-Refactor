package com.example.spot.study.presentation.dto.response;

import com.example.spot.member.domain.Member;
import com.example.spot.study.domain.Study;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class StudyMemberResponseDTO {

    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class JoinDTO {

        private final Long memberId;
        private final StudyResponseDTO.TitleDTO study;

        public static JoinDTO toDTO(Member member, Study study) {
            return JoinDTO.builder()
                    .memberId(member.getId())
                    .study(StudyResponseDTO.TitleDTO.toDTO(study))
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor
    @Builder(access = AccessLevel.PRIVATE)
    public static class WithdrawalDTO {

        private final Long studyId;
        private final String studyName;
        private final Long memberId;
        private final String memberName;

        public static WithdrawalDTO toDTO(Member member, Study study) {
            return WithdrawalDTO.builder()
                    .studyId(study.getId())
                    .studyName(study.getTitle())
                    .memberId(member.getId())
                    .memberName(member.getName())
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class HostDTO {
        private final Boolean isOwned;
        private final HostNameDTO host;
        public static HostDTO toDTO(Boolean isOwned, Member host) {
            return HostDTO.builder()
                    .isOwned(isOwned)
                    .host(HostNameDTO.toDTO(host))
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class HostNameDTO {
        private final Long memberId;
        private final String nickname;
        public static HostNameDTO toDTO(Member host) {
            return HostNameDTO.builder()
                    .memberId(host.getId())
                    .nickname(host.getNickname())
                    .build();
        }
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class StudyMemberListDTO {
        private final long totalElements;
        private final List<StudyMemberDTO> members;

        public StudyMemberListDTO(List<StudyMemberDTO> members){
            this.totalElements = members.size();
            this.members = members;
        }
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class StudyMemberDTO{
        private final Long memberId;
        private final String nickname;
        private final String profileImage;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class ApplyingMemberDTO {
        private final Long memberId;
        private final Long studyId;
        private final String nickname;
        private final String profileImage;
        private final String introduction;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class AppliedStudyDTO {
        private final boolean isApplied;
        private final Long studyId;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class ApplicationStatusDTO {
        private final StudyApplicationStatus status;
        private final LocalDateTime updatedAt;
    }
}
