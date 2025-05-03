package com.example.spot.refactor.vote.domain;

import com.example.spot.refactor.member.domain.Member;
import com.example.spot.refactor.common.entity.BaseEntity;
import com.example.spot.refactor.study.domain.Study;
import com.example.spot.refactor.vote.domain.aggregate.StudyVoteOption;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyVote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Boolean isMultipleChoice;

    @Column(nullable = false)
    private LocalDateTime finishedAt;

    @OneToMany(mappedBy = "studyVote", cascade = CascadeType.ALL)
    private List<StudyVoteOption> studyVoteOptions;

/* ----------------------------- 생성자 ------------------------------------- */

    @Builder
    public StudyVote(Study study, Member member, String title, Boolean isMultipleChoice, LocalDateTime finishedAt) {
        this.study = study;
        this.member = member;
        this.title = title;
        this.isMultipleChoice = isMultipleChoice;
        this.finishedAt = finishedAt;
        this.studyVoteOptions = new ArrayList<>();
    }

/* ----------------------------- 연관관계 메소드 ------------------------------------- */

    public void addOption(StudyVoteOption studyVoteOption) {
        this.studyVoteOptions.add(studyVoteOption);
        studyVoteOption.setStudyVote(this);
    }

    public void updateOption(StudyVoteOption studyVoteOption) {
        studyVoteOptions.set(studyVoteOptions.indexOf(studyVoteOption), studyVoteOption);
    }

    public void updateVote(String title, Boolean isMultipleChoice, LocalDateTime finishedAt) {
        this.title = title;
        this.isMultipleChoice = isMultipleChoice;
        this.finishedAt = finishedAt;
    }
}
