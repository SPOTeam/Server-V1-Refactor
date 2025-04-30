package com.example.spot.legacy.domain.mapping;

import com.example.spot.refactor.domain.member.Member;
import com.example.spot.legacy.domain.Quiz;
import com.example.spot.refactor.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAttendance extends BaseEntity {

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
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

/* ----------------------------- 생성자 ------------------------------------- */

    @Builder
    public MemberAttendance(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

}
