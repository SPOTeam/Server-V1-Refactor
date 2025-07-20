package com.example.spot.story.domain.entity;

import com.example.spot.member.domain.Member;
import com.example.spot.common.entity.BaseEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoryComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String content;

    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer likeCount;

    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer dislikeCount;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    private Boolean isAnonymous;

    @Column
    private Integer anonymousNum;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    private Boolean isDeleted;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private StoryComment parentComment;

    @Builder.Default
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    private List<StoryComment> childrenComment = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "storyComment", cascade = CascadeType.ALL)
    private List<LikedStoryComment> likedComments = new ArrayList<>();

/* ----------------------------- 연관관계 메소드 ------------------------------------- */

    public void addChildrenComment(StoryComment storyComment) {
        childrenComment.add(storyComment);
        storyComment.setParentComment(this);
    }

    public void addLikedComment(LikedStoryComment likedStoryComment) {
        likedComments.add(likedStoryComment);
        likedStoryComment.setStoryComment(this);
    }

    public void deleteLikedComment(LikedStoryComment likedStoryComment) {
        likedComments.remove(likedStoryComment);
    }

    public void deleteComment() {
        content = "삭제된 댓글입니다.";
        isDeleted = Boolean.TRUE;
    }

    public void plusLikeCount() {
        likeCount++;
    }

    public void minusLikeCount() {
        likeCount--;
    }

    public void plusDislikeCount() {
        dislikeCount++;
    }

    public void minusDislikeCount() {
        dislikeCount--;
    }

}
