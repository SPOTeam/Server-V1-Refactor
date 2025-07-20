package com.example.spot.story.domain.entity;

import com.example.spot.common.entity.BaseEntity;
import com.example.spot.member.domain.Member;
import com.example.spot.report.domain.StoryReport;
import com.example.spot.story.domain.enums.StoryCategory;
import com.example.spot.story.domain.dto.request.StoryRequestDTO;
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

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL)
    private List<StoryImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL)
    private List<StoryComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL)
    private List<LikedStory> likedStories = new ArrayList<>();

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL)
    private List<StoryReport> storyReports = new ArrayList<>();


    @Builder
    private Story(Long id, Member member, Study study, Boolean isAnnouncement, LocalDateTime announcedAt,
                  StoryCategory storyCategory, String title, String content, Integer likeNum,
                  Integer hitNum, Integer commentNum
    ) {
        this.id = id;
        this.member = member;
        this.study = study;
        this.isAnnouncement = isAnnouncement;
        this.announcedAt = announcedAt;
        this.storyCategory = storyCategory;
        this.title = title;
        this.content = content;
        this.likeNum = likeNum;
        this.hitNum = hitNum;
        this.commentNum = commentNum;
        this.images = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.likedStories = new ArrayList<>();
        this.storyReports = new ArrayList<>();
    }

    public static Story of(Member member, Study study, Boolean isAnnouncement,
                    StoryCategory storyCategory, String title, String content
    ) {
        Story story = Story.builder()
                .member(member)
                .study(study)
                .isAnnouncement(isAnnouncement)
                .announcedAt(null)
                .storyCategory(storyCategory)
                .title(title)
                .content(content)
                .likeNum(0)
                .hitNum(0)
                .commentNum(0)
                .build();

        if (isAnnouncement) {
            story.setAnnouncedAt(LocalDateTime.now());
        }

        return story;
    }


    /* ----------------------------- 연관관계 메소드 ------------------------------------- */

    public void addImage(StoryImage image) {
        images.add(image);
    }

    public void addComment(StoryComment comment) {
        comments.add(comment);
    }

    public void addLikedPost(LikedStory likedPost) {
        likedStories.add(likedPost);
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

    public void plusHitNum() {
        hitNum++;
    }

    public void plusLikeNum() {
        likeNum++;
    }

    public void minusLikeNum() {
        likeNum--;
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
    }
}
