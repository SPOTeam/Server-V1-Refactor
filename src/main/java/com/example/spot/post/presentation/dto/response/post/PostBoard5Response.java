package com.example.spot.post.presentation.dto.response.post;

import com.example.spot.post.domain.enums.Board;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostBoard5Response {

    private Board boardType;
    private PostDetail5Response postBoard5Response;

    @AllArgsConstructor
    @Getter
    private static class PostDetail5Response {

        private String content;
        private int viewCount;

    }
}
