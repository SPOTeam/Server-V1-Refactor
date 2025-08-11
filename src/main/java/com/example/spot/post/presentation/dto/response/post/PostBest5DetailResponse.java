package com.example.spot.post.presentation.dto.response.post;

import com.example.spot.post.domain.Post;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostBest5DetailResponse {

    private Long postId;
    private int rank;
    private String postTitle;
    private int commentCount;

    public static PostBest5DetailResponse from(Post post, int rank) {
        return PostBest5DetailResponse.builder()
                .postId(post.getId())
                .rank(rank)
                .postTitle(post.getTitle())
                .commentCount(0)
                .build();
    }
}
