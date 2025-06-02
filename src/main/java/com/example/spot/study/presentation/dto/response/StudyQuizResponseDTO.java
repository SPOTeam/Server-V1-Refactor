package com.example.spot.study.presentation.dto.response;

import com.example.spot.schedule.domain.association.Quiz;
import com.example.spot.schedule.domain.association.QuizSubmission;
import com.example.spot.study.domain.association.StudyMember;
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

        public static QuizDTO toDTO(Quiz quiz) {
            return QuizDTO.builder()
                    .quizId(quiz.getId())
                    .question(quiz.getQuestion())
                    .createdAt(quiz.getCreatedAt())
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

        public static AttendanceDTO toDTO(QuizSubmission quizSubmission, Integer tryNum) {
            return AttendanceDTO.builder()
                    .memberId(quizSubmission.getMember().getId())
                    .quizId(quizSubmission.getQuiz().getId())
                    .attendanceId(quizSubmission.getId())
                    .isCorrect(quizSubmission.getIsCorrect())
                    .tryNum(tryNum)
                    .createdAt(quizSubmission.getCreatedAt())
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

        public static AttendanceListDTO toDTO(Quiz quiz, List<StudyMemberDTO> studyMembers) {
            return AttendanceListDTO.builder()
                    .scheduleId(quiz.getSchedule().getId())
                    .quizId(quiz.getId())
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
