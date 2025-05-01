package com.example.spot.refactor.study.domain.aggregate.studyvote;

import com.example.spot.refactor.member.domain.Member;
import com.example.spot.refactor.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyVoteParticipant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_vote_option_id", nullable = false)
    private StudyVoteOption studyVoteOption;

/* ----------------------------- 생성자 ------------------------------------- */

    @Builder
    public StudyVoteParticipant(Member member, StudyVoteOption studyVoteOption) {
        this.member = member;
        this.studyVoteOption = studyVoteOption;
    }

}
