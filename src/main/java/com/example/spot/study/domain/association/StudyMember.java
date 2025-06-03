package com.example.spot.study.domain.association;

import com.example.spot.member.domain.Member;
import com.example.spot.common.entity.BaseEntity;
import com.example.spot.study.domain.Study;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Builder
@AllArgsConstructor
public class StudyMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Setter
    private StudyApplicationStatus status;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    @Setter
    private Boolean isOwned;

    @Column(columnDefinition = "text")
    private String introduction;

    // 해당 유저로 호스트를 위임하는 이유
    @Column(columnDefinition = "text")
    @Setter
    private String reason;

    //== 회원 ==//
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    //== 스터디 ==//
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

/* ----------------------------- 생성자 ------------------------------------- */

    protected StudyMember() {}

    @Builder
    public StudyMember(Boolean isOwned, String introduction, Member member, Study study, StudyApplicationStatus status) {

        this.isOwned = isOwned;
        this.introduction = introduction;
        this.member = member;
        this.study = study;
        this.status = status;
    }


}
