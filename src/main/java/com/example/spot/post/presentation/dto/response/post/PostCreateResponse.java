package com.example.spot.post.presentation.dto.response.post;

import com.example.spot.post.domain.Post;
import com.example.spot.post.domain.enums.Board;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateResponse {

    @Schema(description = "게시글 ID", example = "1")
    private Long id;

    @Schema(
            description = "생성된 게시글 타입입니다.",
            example = "PASS_EXPERIENCE",
            allowableValues = {"PASS_EXPERIENCE", "INFORMATION_SHARING", "COUNSELING", "JOB_TALK", "FREE_TALK",
                    "SPOT_ANNOUNCEMENT"}
    )
    private String type;

    @Schema(description = "생성 시간", example = "2024-01-01T12:34:56")
    private LocalDateTime createdAt;

    public Board getType() {
        return Board.findByValue(type);
    }

    public static PostCreateResponse toDTO(Post post) {

        return PostCreateResponse.builder()
                .id(post.getId())
                .type(post.getBoard().name())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
