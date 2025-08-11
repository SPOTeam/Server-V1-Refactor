package com.example.spot.post.presentation.dto.response.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostLikeResponse {
    private Long postId;
    private Long likeCount;
}
