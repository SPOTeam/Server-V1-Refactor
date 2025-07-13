package com.example.spot.story.domain;

import com.example.spot.common.entity.BaseEntity;
import com.example.spot.member.domain.Member;
import com.example.spot.report.domain.StoryReport;
import com.example.spot.story.domain.association.LikedStory;
import com.example.spot.story.domain.association.StoryComment;
import com.example.spot.story.domain.association.StoryImage;
import com.example.spot.story.domain.enums.StoryCategory;
import com.example.spot.story.web.dto.request.StoryRequestDTO;
import com.example.spot.study.domain.Study;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Story extends BaseEntity {

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
    private StoryCategory storyCategory;

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
    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL)
    private List<StoryImage> images = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL)
    private List<StoryComment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL)
    private List<LikedStory> likedStories = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL)
    private List<StoryReport> storyReports = new ArrayList<>();

    /* ----------------------------- 연관관계 메소드 ------------------------------------- */

    public void addImage(StoryImage image) {
        images.add(image);
        image.setStory(this);
    }

    public void addComment(StoryComment comment) {
        comments.add(comment);
        comment.setStory(this);
    }

    public void addLikedPost(LikedStory likedPost) {
        likedStories.add(likedPost);
        likedPost.setStory(this);
    }

    public void deleteImage(StoryImage image) {
        images.remove(image);
    }

    public void deleteComment(StoryComment comment) {
        comments.remove(comment);
    }

    public void deleteLikedPost(LikedStory likedPost) {
        likedStories.remove(likedPost);
    }

    public void updateComment(StoryComment storyComment) {
        comments.set(comments.indexOf(storyComment), storyComment);
    }

    public void plusHitNum() {
        hitNum++;
        study.updateStudyPost(this);
    }

    public void plusLikeNum() {
        likeNum++;
        study.updateStudyPost(this);
    }

    public void minusLikeNum() {
        likeNum--;
        study.updateStudyPost(this);
    }

    public void addStudyPostReport(StoryReport storyReport) {
        storyReports.add(storyReport);
    }

    public void updatePost(StoryRequestDTO.StoryDTO requestDTO) {
        isAnnouncement = requestDTO.getIsAnnouncement();
        storyCategory = requestDTO.getStoryCategory();
        title = requestDTO.getTitle();
        content = requestDTO.getContent();

        if (isAnnouncement) {
            announcedAt = LocalDateTime.now();
        } else {
            announcedAt = null;
        }

        study.updateStudyPost(this);
    }

    public void updateImage(StoryImage storyImage) {
        images.set(images.indexOf(storyImage), storyImage);
    }
}
