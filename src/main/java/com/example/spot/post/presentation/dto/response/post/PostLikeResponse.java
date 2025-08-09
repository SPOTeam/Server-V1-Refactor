package com.example.spot.post.presentation.dto.response.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostLikeResponse {
    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "좋아요 수", example = "10")
    private Long likeCount;
}
