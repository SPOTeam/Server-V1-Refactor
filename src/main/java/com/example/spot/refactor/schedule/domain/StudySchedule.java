package com.example.spot.refactor.schedule.domain;
import com.example.spot.refactor.member.domain.Member;
import com.example.spot.refactor.common.entity.BaseEntity;
import com.example.spot.refactor.schedule.domain.aggregate.StudyQuiz;
import com.example.spot.refactor.study.domain.aggregate.Study;
import com.example.spot.refactor.schedule.domain.enums.StudySchedulePeriod;
import com.example.spot.refactor.study.presentation.dto.request.ScheduleRequestDTO;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StudySchedule extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder.Default
    @OneToMany(mappedBy = "studySchedule", cascade = CascadeType.ALL)
    private List<StudyQuiz> studyQuizList = new ArrayList<>();

    @Column(nullable = false, length = 20)
    private String title;

    @Column(nullable = false, length = 20)
    private String location;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime finishedAt;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    private Boolean isAllDay;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudySchedulePeriod studySchedulePeriod;

/* ----------------------------- 생성자 ------------------------------------- */

    @Builder
    public StudySchedule(Study study, Member member, String title, String location,
                         LocalDateTime staredAt, LocalDateTime finishedAt,
                         Boolean isAllDay, StudySchedulePeriod studySchedulePeriod) {
        this.study = study;
        this.member = member;
        this.title = title;
        this.location = location;
        this.startedAt = staredAt;
        this.finishedAt = finishedAt;
        this.isAllDay = isAllDay;
        this.studySchedulePeriod = studySchedulePeriod;
        this.studyQuizList = new ArrayList<>();
    }

/* ----------------------------- 메소드 ------------------------------------- */


    public void addQuiz(StudyQuiz studyQuiz) {
        studyQuizList.add(studyQuiz);
        studyQuiz.setStudySchedule(this);
    }
    public void modSchedule(ScheduleRequestDTO.ScheduleDTO scheduleDTO) {
        this.title = scheduleDTO.getTitle();
        this.location = scheduleDTO.getLocation();
        this.startedAt = scheduleDTO.getStartedAt();
        this.finishedAt = scheduleDTO.getFinishedAt();
        this.isAllDay = scheduleDTO.getIsAllDay();
        this.studySchedulePeriod = scheduleDTO.getStudySchedulePeriod();
    }

}
