package com.example.spot.refactor.study.domain;

import com.example.spot.legacy.domain.Notification;
import com.example.spot.refactor.common.entity.BaseEntity;
import com.example.spot.refactor.member.domain.enums.Gender;
import com.example.spot.refactor.member.domain.enums.Status;
import com.example.spot.refactor.story.domain.StudyPost;
import com.example.spot.refactor.schedule.domain.StudySchedule;
import com.example.spot.refactor.study.domain.aggregate.StudyMember;
import com.example.spot.refactor.study.domain.aggregate.StudyRegion;
import com.example.spot.refactor.study.domain.aggregate.StudyTheme;
import com.example.spot.refactor.todo.domain.StudyToDo;
import com.example.spot.refactor.vote.domain.StudyVote;
import com.example.spot.refactor.study.domain.enums.StudyState;
import com.example.spot.refactor.member.domain.association.PreferredStudy;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Study extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private Integer minAge;

    @Column(nullable = false)
    private Integer maxAge;

    @Column(nullable = false)
    private boolean hasFee;

    @Column(nullable = false)
    private Integer fee;

    @Column(nullable = false)
    private String profileImage;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StudyState studyState;

    @Column(length = 30)
    private String performance;

    @Column(nullable = false)
    private Boolean isOnline;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer heartCount;

    @Column(nullable = false)
    private String goal;

    @Column(nullable = false)
    private String introduction;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long hitNum;

    @Column(nullable = false)
    private Long maxPeople;

    @Builder.Default
    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    private List<StudySchedule> studySchedules = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    private List<StudyPost> posts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    private List<StudyVote> studyVotes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    private List<StudyTheme> studyThemes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    private List<StudyMember> memberStudies = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    private List<StudyRegion> regionStudies = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    private List<PreferredStudy> preferredStudies = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    private List<StudyPost> studyPosts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    private List<StudyToDo> studyToDos = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();

/* ----------------------------- 연관관계 메소드 ------------------------------------- */

    public void addMemberStudy(StudyMember studyMember) {
        memberStudies.add(studyMember);
        studyMember.setStudy(this);
    }

    public void addRegionStudy(StudyRegion studyRegion) {
        regionStudies.add(studyRegion);
        studyRegion.setStudy(this);
    }

    public void addStudyTheme(StudyTheme studyTheme) {
        studyThemes.add(studyTheme);
        studyTheme.setStudy(this);
    }

    public void addPreferredStudy(PreferredStudy preferredStudy) {
        preferredStudies.add(preferredStudy);
        preferredStudy.changeStudy(this);
        this.heartCount++;
    }

    public void addSchedule(StudySchedule studySchedule) {
        studySchedules.add(studySchedule);
        studySchedule.setStudy(this);
    }

    public void addVote(StudyVote studyVote) {
        studyVotes.add(studyVote);
        studyVote.setStudy(this);
    }

    public void updateSchedule(StudySchedule studySchedule) {
        studySchedules.set(studySchedules.indexOf(studySchedule), studySchedule);
    }

    public void addStudyPost(StudyPost studyPost) {
        if (this.studyPosts == null) {
            this.studyPosts = new ArrayList<>();
        }
        this.studyPosts.add(studyPost);
        studyPost.setStudy(this);
    }

    public void updateStudyPost(StudyPost studyPost) {
        studyPosts.set(studyPosts.indexOf(studyPost), studyPost);
    }

    public void deleteStudyPost(StudyPost studyPost) {
        studyPosts.remove(studyPost);
    }

    // preferredStudy 삭제
    public void deletePreferredStudy(PreferredStudy preferredStudy) {
        this.heartCount--;
    }

    // hit 증가
    public void increaseHit() {
        this.hitNum++;
    }

    public void updateVote(StudyVote studyVote) {
        studyVotes.set(studyVotes.indexOf(studyVote), studyVote);
    }

    public void deleteVote(StudyVote studyVote) {
        studyVotes.remove(studyVote);
    }

    public void addToDoList(StudyToDo studyToDo) {
        studyToDos.add(studyToDo);
    }

    public void terminateStudy(String performance) {
        this.studyState = StudyState.COMPLETED;
        this.status = Status.OFF;
        this.performance = performance;
    }

    public void updateStudyInfo(
            String title, String introduction, String goal, Boolean isOnline, Boolean hasFee, Integer fee, Integer minAge, Integer maxAge,
            Gender gender, Long maxPeople, String profileImage) {
        this.title = title;
        this.introduction = introduction;
        this.goal = goal;
        this.isOnline = isOnline;
        this.hasFee = hasFee;
        this.fee = fee;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.gender = gender;
        this.maxPeople = maxPeople;
        this.profileImage = profileImage;
    }
}
