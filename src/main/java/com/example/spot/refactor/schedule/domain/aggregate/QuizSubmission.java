package com.example.spot.refactor.schedule.domain.aggregate;

import com.example.spot.refactor.member.domain.Member;
import com.example.spot.refactor.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizSubmission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    private Boolean isCorrect;

    //== 회원 ==//
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    //== 출석 ==//
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_quiz_id", nullable = false)
    private Quiz quiz;

/* ----------------------------- 생성자 ------------------------------------- */

    @Builder
    public QuizSubmission(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

}
