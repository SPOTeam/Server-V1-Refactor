package com.example.spot.refactor.study.domain.aggregate.studypost;

import com.example.spot.refactor.member.domain.Member;
import com.example.spot.refactor.common.entity.BaseEntity;
import com.example.spot.refactor.study.domain.aggregate.*;
import com.example.spot.refactor.study.domain.enums.StudyPostCategory;
import com.example.spot.legacy.web.dto.memberstudy.request.StudyPostRequestDTO;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyPost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    private Boolean isAnnouncement;

    @Setter
    @Column
    private LocalDateTime announcedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StudyPostCategory studyPostCategory;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer likeNum;

    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer hitNum;

    @Setter
    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer commentNum;

    @Builder.Default
    @OneToMany(mappedBy = "studyPost", cascade = CascadeType.ALL)
    private List<StudyPostImage> images = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "studyPost", cascade = CascadeType.ALL)
    private List<StudyPostComment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "studyPost", cascade = CascadeType.ALL)
    private List<LikedStudyPost> likedPosts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "studyPost", cascade = CascadeType.ALL)
    private List<StudyPostReport> studyPostReports = new ArrayList<>();

/* ----------------------------- 연관관계 메소드 ------------------------------------- */

    public void addImage(StudyPostImage image) {
        images.add(image);
        image.setStudyPost(this);
    }

    public void addComment(StudyPostComment comment) {
        comments.add(comment);
        comment.setStudyPost(this);
    }

    public void addLikedPost(LikedStudyPost likedPost) {
        likedPosts.add(likedPost);
        likedPost.setStudyPost(this);
    }

    public void deleteImage(StudyPostImage image) {
        images.remove(image);
    }

    public void deleteComment(StudyPostComment comment) {
        comments.remove(comment);
    }

    public void deleteLikedPost(LikedStudyPost likedPost) {
        likedPosts.remove(likedPost);
    }

    public void updateComment(StudyPostComment studyPostComment) {
        comments.set(comments.indexOf(studyPostComment), studyPostComment);
    }

    public void plusHitNum() {
        hitNum++;
        member.updateStudyPost(this);
        study.updateStudyPost(this);
    }

    public void plusLikeNum() {
        likeNum++;
        member.updateStudyPost(this);
        study.updateStudyPost(this);
    }

    public void minusLikeNum() {
        likeNum--;
        member.updateStudyPost(this);
        study.updateStudyPost(this);
    }

    public void addStudyPostReport(StudyPostReport studyPostReport) {
        studyPostReports.add(studyPostReport);
    }

    public void updatePost(StudyPostRequestDTO.PostDTO requestDTO) {
        isAnnouncement = requestDTO.getIsAnnouncement();
        studyPostCategory = requestDTO.getStudyPostCategory();
        title = requestDTO.getTitle();
        content = requestDTO.getContent();

        if (isAnnouncement) {
            announcedAt = LocalDateTime.now();
        } else {
            announcedAt = null;
        }

        member.updateStudyPost(this);
        study.updateStudyPost(this);
    }

    public void updateImage(StudyPostImage studyPostImage) {
        images.set(images.indexOf(studyPostImage), studyPostImage);
    }
}
