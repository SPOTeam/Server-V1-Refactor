package com.example.spot.refactor.member.domain;

import com.example.spot.legacy.domain.LikedPost;
import com.example.spot.legacy.domain.LikedPostComment;
import com.example.spot.legacy.domain.MemberReport;
import com.example.spot.legacy.domain.Notification;
import com.example.spot.legacy.domain.Post;
import com.example.spot.legacy.domain.PostComment;
import com.example.spot.legacy.domain.PostReport;
import com.example.spot.refactor.study.domain.aggregate.studypost.LikedStudyComment;
import com.example.spot.refactor.study.domain.aggregate.studypost.LikedStudyPost;
import com.example.spot.refactor.study.domain.aggregate.studyschedule.StudyQuiz;
import com.example.spot.legacy.domain.StudyReason;
import com.example.spot.refactor.common.entity.BaseEntity;
import com.example.spot.refactor.member.domain.enums.Carrier;
import com.example.spot.refactor.member.domain.enums.Gender;
import com.example.spot.refactor.member.domain.enums.LoginType;
import com.example.spot.refactor.member.domain.enums.Status;
import com.example.spot.refactor.study.domain.aggregate.studyschedule.StudyQuizSubmission;
import com.example.spot.legacy.domain.mapping.MemberScrap;
import com.example.spot.refactor.study.domain.aggregate.studymember.StudyMember;
import com.example.spot.refactor.member.domain.association.MemberTheme;
import com.example.spot.refactor.study.domain.aggregate.studytodo.StudyToDo;
import com.example.spot.refactor.study.domain.aggregate.studyvote.StudyVote;
import com.example.spot.refactor.study.domain.aggregate.studyvote.StudyVoteParticipant;
import com.example.spot.refactor.member.domain.association.PreferredRegion;
import com.example.spot.refactor.member.domain.association.PreferredStudy;
import com.example.spot.refactor.study.domain.aggregate.studyschedule.StudySchedule;
import com.example.spot.refactor.study.domain.aggregate.studypost.StudyPost;
import com.example.spot.refactor.study.domain.aggregate.studypost.StudyPostComment;
import com.example.spot.refactor.member.presentation.dto.MemberRequestDTO.MemberUpdateDTO;
import jakarta.persistence.*;
import java.util.ArrayList;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(length = 100)
    private String loginId;

    @Setter
    @Column(nullable = false, length = 100)
    private String password;

    @Setter
    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    // 안 쓰면 지워도 될 것 같은데 사이드 이펙트 생길까봐 일단 놔둡니다..!
    @Enumerated(EnumType.STRING)
    @Column
    private Carrier carrier;

    // 안 쓰면 지워도 될 것 같은데 사이드 이펙트 생길까봐 일단 놔둡니다..!
    @Column(length = 15)
    private String phone;

    @Column(nullable = false)
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private String profileImage;

    @Setter
    @Column
    private LocalDateTime inactive;

    @Column(nullable = false)
    private Boolean personalInfo;

    @Column(nullable = false)
    private Boolean idInfo;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    private Boolean isAdmin;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    //== 스터디 희망사유 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StudyReason> studyReasonList = new ArrayList<>();

    //== 알림 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Notification> notificationList = new ArrayList<>();

    //== 해당 회원에 대한 신고 내역 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @Builder.Default
    private List<MemberReport> memberReportList = new ArrayList<>();

    //== 회원이 선호하는 테마 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MemberTheme> memberThemeList = new ArrayList<>();

    //== 회원의 출석 목록 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StudyQuizSubmission> studyQuizSubmissionList = new ArrayList<>();

    //== 회원이 참여하는 스터디 목록 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StudyMember> studyMemberList = new ArrayList<>();

    //== 회원이 찜한 스터디 목록 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PreferredStudy> preferredStudyList = new ArrayList<>();

    //== 회원이 선호하는 지역 목록 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PreferredRegion> preferredRegionList = new ArrayList<>();

   ////== 회원이 작성한 게시글 목록 ==//
   @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
   @Builder.Default
   private List<Post> postList = new ArrayList<>();

   ////== 회원이 좋아요한 게시글 목록 ==//
   @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
   @Builder.Default
   private List<LikedPost> likedPostList = new ArrayList<>();

   ////== 회원이 선호하는 지역 목록 ==//
   @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
   @Builder.Default
   private List<PostReport> postReportList = new ArrayList<>();

   ////== 회원이 스크랩한 게시글 목록 ==//
   @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
   @Builder.Default
   private List<MemberScrap> memberScrapList = new ArrayList<>();

   ////== 회원이 작성한 게시글 댓글 목록 ==//
   @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
   @Builder.Default
   private List<PostComment> postCommentList = new ArrayList<>();

   ////== 회원이 좋아요한 게시글 댓글 목록 ==//
   @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
   @Builder.Default
   private List<LikedPostComment> likedCommentList = new ArrayList<>();

    //== 회원이 작성한 스터디 게시글 목록 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StudyPost> studyPostList = new ArrayList<>();

    //== 회원이 좋아요한 스터디 게시글 목록 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LikedStudyPost> likedStudyPostList = new ArrayList<>();

    //== 회원이 작성한 스터디 게시글 댓글 목록 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StudyPostComment> studyPostCommentList = new ArrayList<>();

    //== 회원이 좋아요한 게시글 댓글 목록 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LikedStudyComment> likedStudyCommentList = new ArrayList<>();

    //== 회원이 생성한 투표 목록 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StudyVote> studyVoteList = new ArrayList<>();

    //== 회원이 투표한 항목 목록 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StudyVoteParticipant> studyVoteParticipantList = new ArrayList<>();

    //== 회원이 선호하는 지역 목록 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PreferredRegion> regions = new ArrayList<>();

    //== 회원이 생성한 스터디 퀴즈 목록 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StudyQuiz> studyQuizList = new ArrayList<>();

    //== 회원이 생성한 스터디 일정 목록 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StudySchedule> studyScheduleList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StudyToDo> studyToDos = new ArrayList<>();

/* ----------------------------- 연관관계 메소드 ------------------------------------- */

    public void addMemberStudy(StudyMember studyMember) {
        studyMemberList.add(studyMember);
        studyMember.setMember(this);
    }

    public void addPreferredRegion(PreferredRegion preferredRegion) {
        if (this.regions == null) {
            this.regions = new ArrayList<>(); // 재초기화 (안정성 추가)
        }
        this.regions.add(preferredRegion);
        preferredRegion.setMember(this); // 양방향 관계 설정
    }

    public void addMemberTheme(MemberTheme memberTheme) {
        if (this.memberThemeList == null) {
            this.memberThemeList = new ArrayList<>(); // 재초기화 (안정성 추가)
        }
        this.memberThemeList.add(memberTheme);
        memberTheme.setMember(this); // 양방향 관계 설정
    }

    public void addMemberAttendance(StudyQuizSubmission studyQuizSubmission) {
        if (this.studyQuizSubmissionList == null) {
            this.studyQuizSubmissionList = new ArrayList<>();
        }
        this.studyQuizSubmissionList.add(studyQuizSubmission);
        studyQuizSubmission.setMember(this);
    }

    public void addVote(StudyVote studyVote) {
        if (this.studyVoteList == null) {
            this.studyVoteList = new ArrayList<>();
        }
        this.studyVoteList.add(studyVote);
        studyVote.setMember(this);
    }

    public void updateThemes(List<MemberTheme> memberThemes) {
        this.memberThemeList.clear();
        this.memberThemeList.addAll(memberThemes);
    }
    public void updateRegions(List<PreferredRegion> preferredRegions) {
        this.preferredRegionList.clear();
        this.preferredRegionList.addAll(preferredRegions);
    }

    public void updateReasons(List<StudyReason> studyReasons) {
        this.studyReasonList.clear();
        this.studyReasonList.addAll(studyReasons);
    }

    public void updateTerm(Boolean personalInfo, Boolean idInfo) {
        this.personalInfo = personalInfo;
        this.idInfo = idInfo;
    }

    public void updateInfo(MemberUpdateDTO req) {
        this.name = req.getName();
        this.phone = req.getPhone();
        this.birth = req.getBirth();
        this.carrier = req.getCarrier();
        this.idInfo = req.isIdInfo();
        this.personalInfo = req.isPersonalInfo();
        this.profileImage = req.getProfileImage();
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void addStudyPost(StudyPost studyPost) {
        if (this.studyPostList == null) {
            this.studyPostList = new ArrayList<>();
        }
        this.studyPostList.add(studyPost);
        studyPost.setMember(this);
    }

    public void addStudyLikedComment(LikedStudyComment likedStudyComment) {
        if (this.likedCommentList == null) {
            this.likedCommentList = new ArrayList<>();
        }
        this.likedStudyCommentList.add(likedStudyComment);
        likedStudyComment.setMember(this);
    }

    public void addMemberVote(StudyVoteParticipant studyVoteParticipant) {
        if (this.studyVoteParticipantList == null) {
            this.studyVoteParticipantList = new ArrayList<>();
        }
        this.studyVoteParticipantList.add(studyVoteParticipant);
        studyVoteParticipant.setMember(this);
    }

    public void updateVote(StudyVote studyVote) {
        studyVoteList.set(studyVoteList.indexOf(studyVote), studyVote);
    }

    public void deleteStudyPost(StudyPost studyPost) {
        this.studyPostList.remove(studyPost);
    }

    public void updateStudyPost(StudyPost studyPost) {
        studyPostList.set(studyPostList.indexOf(studyPost), studyPost);
    }

    public void updateComment(StudyPostComment studyPostComment) {
        studyPostCommentList.set(studyPostCommentList.indexOf(studyPostComment), studyPostComment);
    }

    public void addStudyLikedPost(LikedStudyPost likedStudyPost) {
        if (this.likedStudyPostList == null) {
            this.likedStudyPostList = new ArrayList<>();
        }
        this.likedStudyPostList.add(likedStudyPost);
        likedStudyPost.setMember(this);
    }

    public void deleteStudyLikedPost(LikedStudyPost likedStudyPost) {
        this.likedStudyPostList.remove(likedStudyPost);
    }

    public void deleteStudyLikedComment(LikedStudyComment likedStudyComment) {
        this.likedStudyCommentList.remove(likedStudyComment);
    }

    public void addComment(StudyPostComment studyPostComment) {
        this.studyPostCommentList.add(studyPostComment);
    }

    public void deleteVote(StudyVote studyVote) {
        this.studyVoteList.remove(studyVote);
    }

    public void addQuiz(StudyQuiz studyQuiz) {
        this.studyQuizList.add(studyQuiz);
        studyQuiz.setMember(this);
    }

    public void addSchedule(StudySchedule studySchedule) {
        this.studyScheduleList.add(studySchedule);
        studySchedule.setMember(this);

    }

    public void updateSchedule(StudySchedule studySchedule) {
        studyScheduleList.set(studyScheduleList.indexOf(studySchedule), studySchedule);
    }

    public void toAdmin() {
        this.isAdmin = true;
    }

    public void addMemberReport(MemberReport memberReport) {
        this.memberReportList.add(memberReport);
    }

    public void addToDoList(StudyToDo studyToDo) {
        this.studyToDos.add(studyToDo);
    }

}
