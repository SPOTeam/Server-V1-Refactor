package com.example.spot.study.presentation.dto.response;

import com.example.spot.member.domain.Member;
import lombok.*;

import java.util.List;

@Getter
public class StudyMemberResponseDTO {

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class HostDTO {
        private final Boolean isOwned;
        private final HostInfoDTO host;
        public static HostDTO toDTO(Boolean isOwned, Member host) {
            return HostDTO.builder()
                    .isOwned(isOwned)
                    .host(HostInfoDTO.toDTO(host))
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    private static class HostInfoDTO {
        private final Long memberId;
        private final String nickname;
        public static HostInfoDTO toDTO(Member host) {
            return HostInfoDTO.builder()
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
}
