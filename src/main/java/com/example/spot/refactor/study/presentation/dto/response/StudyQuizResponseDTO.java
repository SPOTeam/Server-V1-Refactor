package com.example.spot.refactor.study.presentation.dto.response;

import com.example.spot.refactor.study.domain.aggregate.studyschedule.StudyQuiz;
import com.example.spot.refactor.study.domain.aggregate.studyschedule.StudyQuizSubmission;
import com.example.spot.refactor.study.domain.aggregate.StudyMember;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class StudyQuizResponseDTO {

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class QuizDTO {

        private final Long quizId;
        private final String question;
        private final LocalDateTime createdAt;

        public static QuizDTO toDTO(StudyQuiz studyQuiz) {
            return QuizDTO.builder()
                    .quizId(studyQuiz.getId())
                    .question(studyQuiz.getQuestion())
                    .createdAt(studyQuiz.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class AttendanceDTO {

        private final Long memberId;
        private final Long quizId;
        private final Long attendanceId;
        private final Boolean isCorrect;
        private final Integer tryNum;
        private final LocalDateTime createdAt;

        public static AttendanceDTO toDTO(StudyQuizSubmission studyQuizSubmission, Integer tryNum) {
            return AttendanceDTO.builder()
                    .memberId(studyQuizSubmission.getMember().getId())
                    .quizId(studyQuizSubmission.getStudyQuiz().getId())
                    .attendanceId(studyQuizSubmission.getId())
                    .isCorrect(studyQuizSubmission.getIsCorrect())
                    .tryNum(tryNum)
                    .createdAt(studyQuizSubmission.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class AttendanceListDTO {

        private final Long scheduleId;
        private final Long quizId;
        private final List<StudyMemberDTO> studyMembers;

        public static AttendanceListDTO toDTO(StudyQuiz studyQuiz, List<StudyMemberDTO> studyMembers) {
            return AttendanceListDTO.builder()
                    .scheduleId(studyQuiz.getStudySchedule().getId())
                    .quizId(studyQuiz.getId())
                    .studyMembers(studyMembers)
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class StudyMemberDTO {

        private final Long memberId;
        private final String name;
        private final String profileImage;
        private final Boolean isOwned;
        private final Boolean isAttending;

        public static StudyMemberDTO toDTO(StudyMember studyMember, Boolean isAttending) {
            return StudyMemberDTO.builder()
                    .memberId(studyMember.getMember().getId())
                    .name(studyMember.getMember().getName())
                    .profileImage(studyMember.getMember().getProfileImage())
                    .isOwned(studyMember.getIsOwned())
                    .isAttending(isAttending)
                    .build();
        }

    }
}
