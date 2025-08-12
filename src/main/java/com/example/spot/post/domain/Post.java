package com.example.spot.post.domain;

import com.example.spot.common.entity.BaseEntity;
import com.example.spot.member.domain.Member;
import com.example.spot.post.domain.association.LikedPost;
import com.example.spot.post.domain.enums.Board;
import com.example.spot.post.presentation.dto.request.post.PostUpdateRequest;
import jakarta.persistence.CascadeType;
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
import org.springframework.util.StringUtils;

@Builder
@AllArgsConstructor
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isAdmin;

    private boolean isAnonymous;

    private String title;

    private String content;

    private String image;

    @Enumerated(EnumType.STRING)
    private Board board;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @Builder.Default
    private List<LikedPost> likedPostList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @Builder.Default
    private List<PostComment> postCommentList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void edit(PostUpdateRequest request, List<String> images, String existingImage) {
        if (StringUtils.hasText(request.getTitle())) {
            this.title = request.getTitle();
        }

        if (StringUtils.hasText(request.getContent())) {
            this.content = request.getContent();
        }

        this.isAnonymous = request.isAnonymous();

        updateImage(images, existingImage);

        if (request.getType() != null) {
            this.board = request.getType();
        }

        if (request.getType() != null) {
            this.board = request.getType();
        }
    }

    private void updateImage(List<String> images, String existingImage) {
        if (StringUtils.hasText(existingImage)) {
            this.image = existingImage;
        } else if (images != null && !images.isEmpty() && StringUtils.hasText(images.get(0))) {
            this.image = images.get(0);
        } else {
            this.image = null;
        }
    }


    public void setCreatedAt(LocalDateTime createdAt) {
        super.setCreatedAt(createdAt);
    }

}
