package com.example.spot.post.presentation.dto.response.post;

import com.example.spot.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PostRepresentativeDetailResponse {

    private Long postId;
    private String postType;
    private String postTitle;
    private int commentCount;

    public static PostRepresentativeDetailResponse toDTO(Post post) {
        return PostRepresentativeDetailResponse.builder()
                .postId(post.getId())
                .postType(post.getBoard().name())
                .postTitle(post.getTitle())
                .commentCount(post.getPostCommentList().size())
                .build();
    }
}
