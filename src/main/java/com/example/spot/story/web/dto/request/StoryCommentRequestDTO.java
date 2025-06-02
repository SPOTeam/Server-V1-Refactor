package com.example.spot.story.web.dto.request;

import com.example.spot.common.presentation.validator.TextLength;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class StoryCommentRequestDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentDTO {

        private Boolean isAnonymous;

        @TextLength(min = 1, max = 255)
        private String content;
    }
}
