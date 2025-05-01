package com.example.spot.refactor.study.domain.aggregate.studyvote;
import com.example.spot.refactor.common.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

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
public class StudyVoteOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_vote_id", nullable = false)
    private StudyVote studyVote;

    @Setter
    private String content;

    @OneToMany(mappedBy = "studyVoteOption", cascade = CascadeType.ALL)
    private List<StudyVoteParticipant> studyVoteParticipants;

/* ----------------------------- 생성자 ------------------------------------- */

    @Builder
    public StudyVoteOption(StudyVote studyVote, String content) {
        this.studyVote = studyVote;
        this.content = content;
        this.studyVoteParticipants = new ArrayList<>();
    }

/* ----------------------------- 연관관계 메소드 ------------------------------------- */

    public void addMemberVote(StudyVoteParticipant studyVoteParticipant) {
        if (studyVoteParticipants == null) {
            studyVoteParticipants = new ArrayList<>();
        }
        studyVoteParticipants.add(studyVoteParticipant);
        studyVoteParticipant.setStudyVoteOption(this);
    }

    public void deleteAllMemberVotes() {
        studyVoteParticipants.clear();
    }

}
