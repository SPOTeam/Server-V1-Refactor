package com.example.spot.vote.presentation.dto.response;

import com.example.spot.member.domain.Member;
import com.example.spot.vote.domain.Vote;
import com.example.spot.vote.domain.association.VoteParticipant;
import com.example.spot.vote.domain.association.VoteOption;
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

        public static VotePreviewDTO toDTO(Vote vote) {
            return VotePreviewDTO.builder()
                    .voteId(vote.getId())
                    .title(vote.getTitle())
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

        public static VotedOptionDTO toDTO(Vote vote, Member member, List<VoteParticipant> voteParticipants) {
            return VotedOptionDTO.builder()
                    .voteId(vote.getId())
                    .memberId(member.getId())
                    .votedOptions(voteParticipants.stream()
                            .map(memberVote -> OptionDTO.toDTO(memberVote.getVoteOption()))
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

        public static VoteInfoDTO toDTO(Vote vote, Boolean isParticipated) {
            return VoteInfoDTO.builder()
                    .voteId(vote.getId())
                    .title(vote.getTitle())
                    .finishedAt(vote.getFinishedAt())
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

        public static VoteDTO toDTO(Vote vote, Member member) {
            return VoteDTO.builder()
                    .voteId(vote.getId())
                    .creator(MemberDTO.toDTO(vote.getMember()))
                    .title(vote.getTitle())
                    .options(vote.getVoteOptions().stream()
                            .map(OptionDTO::toDTO)
                            .toList())
                    .isMultipleChoice(vote.getIsMultipleChoice())
                    .finishedAt(vote.getFinishedAt())
                    .isParticipated(vote.getVoteOptions().stream()
                            .flatMap(option -> option.getVoteParticipants().stream())
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

        public static CompletedVoteDTO toDTO(Vote vote) {
            return CompletedVoteDTO.builder()
                    .voteId(vote.getId())
                    .creator(MemberDTO.toDTO(vote.getMember()))
                    .title(vote.getTitle())
                    .optionCounts(vote.getVoteOptions().stream()
                            .map(VotedOptionCountDTO::toDTO)
                            .toList())
                    .totalParticipants(vote.getVoteOptions().stream()
                            .map(VotedOptionCountDTO::toDTO)
                            .mapToInt(VotedOptionCountDTO::getCount)
                            .sum())
                    .finishedAt(vote.getFinishedAt())
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

        public static CompletedVoteDetailDTO toDTO(Vote vote) {
            return CompletedVoteDetailDTO.builder()
                    .voteId(vote.getId())
                    .title(vote.getTitle())
                    .optionVoters(vote.getVoteOptions().stream()
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

        public static VotedOptionCountDTO toDTO(VoteOption voteOption) {
            return VotedOptionCountDTO.builder()
                    .optionId(voteOption.getId())
                    .content(voteOption.getContent())
                    .count(voteOption.getVoteParticipants().size())
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

        public static OptionVoterDTO toDTO(VoteOption voteOption) {
            return OptionVoterDTO.builder()
                    .optionId(voteOption.getId())
                    .content(voteOption.getContent())
                    .count(voteOption.getVoteParticipants().size())
                    .voters(voteOption.getVoteParticipants().stream()
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

        public static OptionDTO toDTO(VoteOption voteOption) {
            return OptionDTO.builder()
                    .optionId(voteOption.getId())
                    .content(voteOption.getContent())
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
