package com.example.spot.post.presentation.dto.response.post;

import com.example.spot.post.domain.Post;
import com.example.spot.post.domain.enums.Board;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateResponse {

    private Long id;
    private String type;
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
