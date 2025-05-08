package com.example.spot.refactor.post.presentation.dto;

import com.example.spot.refactor.post.domain.enums.Board;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostBoard5Response {

    @Schema(
            description = "게시글 타입입니다. 아래와 같이 작성해주세요."
    )
    private Board boardType;

    private PostDetail5Response postBoard5Response;

    @AllArgsConstructor
    @Getter
    private static class PostDetail5Response{

        @Schema(description = "게시글 내용입니다.",
                format = "string")
        private String content;

        @Schema(
                description = "조회 수입니다.",
                format = "int"
        )
        private int viewCount;

    }
}
