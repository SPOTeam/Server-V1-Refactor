package com.example.spot.refactor.study.domain.aggregate.studyschedule;

import com.example.spot.refactor.member.domain.Member;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, length = 20)
    private String question;

    @Column(nullable = false, length = 10)
    private String answer;

    //== 출석 회원 목록 ==//
    @OneToMany(mappedBy = "studyQuiz", cascade = CascadeType.ALL)
    private List<StudyQuizSubmission> studyQuizSubmissionList;

    //== 해당 퀴즈를 생성한 일정 ==//
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private StudySchedule studySchedule;

    //== 퀴즈 생성자 ==//
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column
    private LocalDateTime createdAt;

    @Column
    @LastModifiedDate
    private LocalDateTime updatedAt;

/* ----------------------------- 생성자 ------------------------------------- */

    @Builder
    public StudyQuiz(StudySchedule studySchedule, Member member, String question, String answer, LocalDateTime createdAt) {
        this.studySchedule = studySchedule;
        this.member = member;
        this.question = question;
        this.answer = answer;
        this.createdAt = createdAt;
        this.studyQuizSubmissionList = new ArrayList<>();
    }

/* ----------------------------- 연관관계 메소드 ------------------------------------- */

    public void addMemberAttendance(StudyQuizSubmission studyQuizSubmission) {
        studyQuizSubmissionList.add(studyQuizSubmission);
        studyQuizSubmission.setStudyQuiz(this);
    }

    public void deleteMemberAttendance(StudyQuizSubmission studyQuizSubmission) {
        studyQuizSubmissionList.remove(studyQuizSubmission);
    }
}
