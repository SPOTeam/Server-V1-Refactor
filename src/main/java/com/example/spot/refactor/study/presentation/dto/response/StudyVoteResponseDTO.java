package com.example.spot.refactor.study.presentation.dto.response;

import com.example.spot.refactor.member.domain.Member;
import com.example.spot.refactor.study.domain.aggregate.studyvote.StudyVoteParticipant;
import com.example.spot.refactor.study.domain.aggregate.studyvote.StudyVoteOption;
import com.example.spot.refactor.study.domain.aggregate.studyvote.StudyVote;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class StudyVoteResponseDTO {

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class VotePreviewDTO {

        private final Long voteId;
        private final String title;

        public static VotePreviewDTO toDTO(StudyVote studyVote) {
            return VotePreviewDTO.builder()
                    .voteId(studyVote.getId())
                    .title(studyVote.getTitle())
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class VotedOptionDTO {

        private final Long voteId;
        private final Long memberId;
        private final List<OptionDTO> votedOptions;

        public static VotedOptionDTO toDTO(StudyVote studyVote, Member member, List<StudyVoteParticipant> studyVoteParticipants) {
            return VotedOptionDTO.builder()
                    .voteId(studyVote.getId())
                    .memberId(member.getId())
                    .votedOptions(studyVoteParticipants.stream()
                            .map(memberVote -> OptionDTO.toDTO(memberVote.getStudyVoteOption()))
                            .toList())
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class VoteListDTO {

        private final Long studyId;
        private final List<VoteInfoDTO> votesInProgress;
        private final List<VoteInfoDTO> votesInCompletion;

        public static VoteListDTO toDTO(Long studyId, List<VoteInfoDTO> votesInProgress, List<VoteInfoDTO> votesInCompletion) {
            return VoteListDTO.builder()
                    .studyId(studyId)
                    .votesInProgress(votesInProgress)
                    .votesInCompletion(votesInCompletion)
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class VoteInfoDTO {

        private final Long voteId;
        private final String title;
        private final LocalDateTime finishedAt;
        private final Boolean isParticipated;

        public static VoteInfoDTO toDTO(StudyVote studyVote, Boolean isParticipated) {
            return VoteInfoDTO.builder()
                    .voteId(studyVote.getId())
                    .title(studyVote.getTitle())
                    .finishedAt(studyVote.getFinishedAt())
                    .isParticipated(isParticipated)
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class VoteDTO {

        private final Long voteId;
        private final MemberDTO creator;
        private final String title;
        private final List<OptionDTO> options;
        private final Boolean isMultipleChoice;
        private final LocalDateTime finishedAt;
        private final Boolean isParticipated;

        public static VoteDTO toDTO(StudyVote studyVote, Member member) {
            return VoteDTO.builder()
                    .voteId(studyVote.getId())
                    .creator(MemberDTO.toDTO(studyVote.getMember()))
                    .title(studyVote.getTitle())
                    .options(studyVote.getStudyVoteOptions().stream()
                            .map(OptionDTO::toDTO)
                            .toList())
                    .isMultipleChoice(studyVote.getIsMultipleChoice())
                    .finishedAt(studyVote.getFinishedAt())
                    .isParticipated(studyVote.getStudyVoteOptions().stream()
                            .flatMap(option -> option.getStudyVoteParticipants().stream())
                            .anyMatch(memberVote -> memberVote.getMember().equals(member)))
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class CompletedVoteDTO {

        private final Long voteId;
        private final MemberDTO creator;
        private final String title;
        private final List<VotedOptionCountDTO> optionCounts;
        private final int totalParticipants;
        private final LocalDateTime finishedAt;

        public static CompletedVoteDTO toDTO(StudyVote studyVote) {
            return CompletedVoteDTO.builder()
                    .voteId(studyVote.getId())
                    .creator(MemberDTO.toDTO(studyVote.getMember()))
                    .title(studyVote.getTitle())
                    .optionCounts(studyVote.getStudyVoteOptions().stream()
                            .map(VotedOptionCountDTO::toDTO)
                            .toList())
                    .totalParticipants(studyVote.getStudyVoteOptions().stream()
                            .map(VotedOptionCountDTO::toDTO)
                            .mapToInt(VotedOptionCountDTO::getCount)
                            .sum())
                    .finishedAt(studyVote.getFinishedAt())
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class CompletedVoteDetailDTO {

        private final Long voteId;
        private final String title;
        private final List<OptionVoterDTO> optionVoters;

        public static CompletedVoteDetailDTO toDTO(StudyVote studyVote) {
            return CompletedVoteDetailDTO.builder()
                    .voteId(studyVote.getId())
                    .title(studyVote.getTitle())
                    .optionVoters(studyVote.getStudyVoteOptions().stream()
                            .map(OptionVoterDTO::toDTO)
                            .toList())
                    .build();
        }
    }

/* ----------------------------- Private ------------------------------------- */

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    private static class VotedOptionCountDTO {

        private final Long optionId;
        private final String content;
        private final int count;

        public static VotedOptionCountDTO toDTO(StudyVoteOption studyVoteOption) {
            return VotedOptionCountDTO.builder()
                    .optionId(studyVoteOption.getId())
                    .content(studyVoteOption.getContent())
                    .count(studyVoteOption.getStudyVoteParticipants().size())
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    private static class OptionVoterDTO {

        private final Long optionId;
        private final String content;
        private final int count;
        private final List<MemberDTO> voters;

        public static OptionVoterDTO toDTO(StudyVoteOption studyVoteOption) {
            return OptionVoterDTO.builder()
                    .optionId(studyVoteOption.getId())
                    .content(studyVoteOption.getContent())
                    .count(studyVoteOption.getStudyVoteParticipants().size())
                    .voters(studyVoteOption.getStudyVoteParticipants().stream()
                            .map(memberVote -> MemberDTO.toDTO(memberVote.getMember()))
                            .toList())
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    private static class OptionDTO {

        private final Long optionId;
        private final String content;

        public static OptionDTO toDTO(StudyVoteOption studyVoteOption) {
            return OptionDTO.builder()
                    .optionId(studyVoteOption.getId())
                    .content(studyVoteOption.getContent())
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    private static class MemberDTO {

        private final Long memberId;
        private final String name;
        private final String profileImage;

        public static MemberDTO toDTO(Member member) {
            return MemberDTO.builder()
                    .memberId(member.getId())
                    .name(member.getName())
                    .profileImage(member.getProfileImage())
                    .build();
        }
    }


}
