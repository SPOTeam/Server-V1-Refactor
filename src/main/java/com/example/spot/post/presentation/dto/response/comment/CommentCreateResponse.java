package com.example.spot.post.presentation.dto.response.comment;

import com.example.spot.post.domain.PostComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class CommentCreateResponse {

    private Long id;
    private String content;
    private String writer;

    public static CommentCreateResponse toDTO(PostComment comment) {
        return CommentCreateResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .writer(comment.isAnonymous() ? "익명" : comment.getMember().getNickname())
                .build();
    }
}
