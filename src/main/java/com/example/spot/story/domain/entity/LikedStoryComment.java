package com.example.spot.story.domain.entity;

import com.example.spot.member.domain.Member;
import com.example.spot.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikedStoryComment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 1")
    private Boolean isLiked;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_comment_id", nullable = false)
    private StoryComment storyComment;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

}
