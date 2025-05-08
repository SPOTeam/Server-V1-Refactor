package com.example.spot.refactor.member.domain;

import com.example.spot.refactor.comment.domain.PostComment;
import com.example.spot.refactor.comment.domain.association.LikedPostComment;
import com.example.spot.refactor.notification.domain.Notification;
import com.example.spot.refactor.post.domain.Post;
import com.example.spot.refactor.post.domain.association.LikedPost;
import com.example.spot.refactor.report.domain.MemberReport;
import com.example.spot.refactor.report.domain.PostReport;
import com.example.spot.refactor.schedule.domain.Schedule;
import com.example.spot.refactor.schedule.domain.aggregate.Quiz;
import com.example.spot.refactor.story.domain.Story;
import com.example.spot.refactor.story.domain.aggregate.LikedStoryComment;
import com.example.spot.refactor.story.domain.aggregate.LikedStory;
import com.example.spot.refactor.member.domain.association.StudyJoinReason;
import com.example.spot.refactor.common.entity.BaseEntity;
import com.example.spot.refactor.member.domain.enums.Carrier;
import com.example.spot.refactor.member.domain.enums.Gender;
import com.example.spot.refactor.member.domain.enums.LoginType;
import com.example.spot.refactor.member.domain.enums.Status;
import com.example.spot.refactor.schedule.domain.aggregate.QuizSubmission;
import com.example.spot.refactor.post.domain.association.MemberScrap;
import com.example.spot.refactor.study.domain.aggregate.StudyMember;
import com.example.spot.refactor.member.domain.association.MemberTheme;
import com.example.spot.refactor.todo.domain.ToDo;
import com.example.spot.refactor.vote.domain.Vote;
import com.example.spot.refactor.vote.domain.aggregate.VoteParticipant;
import com.example.spot.refactor.member.domain.association.PreferredRegion;
import com.example.spot.refactor.member.domain.association.PreferredStudy;
import com.example.spot.refactor.story.domain.aggregate.StoryComment;
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
    private List<StudyJoinReason> studyJoinReasonList = new ArrayList<>();

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
    private List<QuizSubmission> quizSubmissionList = new ArrayList<>();

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
    private List<Story> storyList = new ArrayList<>();

    //== 회원이 좋아요한 스터디 게시글 목록 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LikedStory> likedStoryList = new ArrayList<>();

    //== 회원이 작성한 스터디 게시글 댓글 목록 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StoryComment> storyCommentList = new ArrayList<>();

    //== 회원이 좋아요한 게시글 댓글 목록 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LikedStoryComment> likedStoryCommentList = new ArrayList<>();

    //== 회원이 생성한 투표 목록 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Vote> voteList = new ArrayList<>();

    //== 회원이 투표한 항목 목록 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VoteParticipant> voteParticipantList = new ArrayList<>();

    //== 회원이 선호하는 지역 목록 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PreferredRegion> regions = new ArrayList<>();

    //== 회원이 생성한 스터디 퀴즈 목록 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Quiz> quizList = new ArrayList<>();

    //== 회원이 생성한 스터디 일정 목록 ==//
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Schedule> scheduleList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ToDo> toDos = new ArrayList<>();

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

    public void addMemberAttendance(QuizSubmission quizSubmission) {
        if (this.quizSubmissionList == null) {
            this.quizSubmissionList = new ArrayList<>();
        }
        this.quizSubmissionList.add(quizSubmission);
        quizSubmission.setMember(this);
    }

    public void addVote(Vote vote) {
        if (this.voteList == null) {
            this.voteList = new ArrayList<>();
        }
        this.voteList.add(vote);
        vote.setMember(this);
    }

    public void updateThemes(List<MemberTheme> memberThemes) {
        this.memberThemeList.clear();
        this.memberThemeList.addAll(memberThemes);
    }
    public void updateRegions(List<PreferredRegion> preferredRegions) {
        this.preferredRegionList.clear();
        this.preferredRegionList.addAll(preferredRegions);
    }

    public void updateReasons(List<StudyJoinReason> studyJoinReasons) {
        this.studyJoinReasonList.clear();
        this.studyJoinReasonList.addAll(studyJoinReasons);
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

    public void addStudyPost(Story story) {
        if (this.storyList == null) {
            this.storyList = new ArrayList<>();
        }
        this.storyList.add(story);
        story.setMember(this);
    }

    public void addStudyLikedComment(LikedStoryComment likedStoryComment) {
        if (this.likedCommentList == null) {
            this.likedCommentList = new ArrayList<>();
        }
        this.likedStoryCommentList.add(likedStoryComment);
        likedStoryComment.setMember(this);
    }

    public void addMemberVote(VoteParticipant voteParticipant) {
        if (this.voteParticipantList == null) {
            this.voteParticipantList = new ArrayList<>();
        }
        this.voteParticipantList.add(voteParticipant);
        voteParticipant.setMember(this);
    }

    public void updateVote(Vote vote) {
        voteList.set(voteList.indexOf(vote), vote);
    }

    public void deleteStudyPost(Story story) {
        this.storyList.remove(story);
    }

    public void updateStudyPost(Story story) {
        storyList.set(storyList.indexOf(story), story);
    }

    public void updateComment(StoryComment storyComment) {
        storyCommentList.set(storyCommentList.indexOf(storyComment), storyComment);
    }

    public void addStudyLikedPost(LikedStory likedStory) {
        if (this.likedStoryList == null) {
            this.likedStoryList = new ArrayList<>();
        }
        this.likedStoryList.add(likedStory);
        likedStory.setMember(this);
    }

    public void deleteStudyLikedPost(LikedStory likedStory) {
        this.likedStoryList.remove(likedStory);
    }

    public void deleteStudyLikedComment(LikedStoryComment likedStoryComment) {
        this.likedStoryCommentList.remove(likedStoryComment);
    }

    public void addComment(StoryComment storyComment) {
        this.storyCommentList.add(storyComment);
    }

    public void deleteVote(Vote vote) {
        this.voteList.remove(vote);
    }

    public void addQuiz(Quiz quiz) {
        this.quizList.add(quiz);
        quiz.setMember(this);
    }

    public void addSchedule(Schedule schedule) {
        this.scheduleList.add(schedule);
        schedule.setMember(this);

    }

    public void updateSchedule(Schedule schedule) {
        scheduleList.set(scheduleList.indexOf(schedule), schedule);
    }

    public void toAdmin() {
        this.isAdmin = true;
    }

    public void addMemberReport(MemberReport memberReport) {
        this.memberReportList.add(memberReport);
    }

    public void addToDoList(ToDo toDo) {
        this.toDos.add(toDo);
    }

}
