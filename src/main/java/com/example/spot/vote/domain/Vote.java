package com.example.spot.vote.domain;

import com.example.spot.member.domain.Member;
import com.example.spot.common.entity.BaseEntity;
import com.example.spot.study.domain.Study;
import com.example.spot.vote.domain.aggregate.VoteOption;
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
public class Vote extends BaseEntity {

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

    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL)
    private List<VoteOption> voteOptions;

/* ----------------------------- 생성자 ------------------------------------- */

    @Builder
    public Vote(Study study, Member member, String title, Boolean isMultipleChoice, LocalDateTime finishedAt) {
        this.study = study;
        this.member = member;
        this.title = title;
        this.isMultipleChoice = isMultipleChoice;
        this.finishedAt = finishedAt;
        this.voteOptions = new ArrayList<>();
    }

/* ----------------------------- 연관관계 메소드 ------------------------------------- */

    public void addOption(VoteOption voteOption) {
        this.voteOptions.add(voteOption);
        voteOption.setVote(this);
    }

    public void updateOption(VoteOption voteOption) {
        voteOptions.set(voteOptions.indexOf(voteOption), voteOption);
    }

    public void updateVote(String title, Boolean isMultipleChoice, LocalDateTime finishedAt) {
        this.title = title;
        this.isMultipleChoice = isMultipleChoice;
        this.finishedAt = finishedAt;
    }
}
