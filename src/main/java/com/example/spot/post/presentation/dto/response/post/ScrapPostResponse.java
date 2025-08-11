package com.example.spot.post.presentation.dto.response.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ScrapPostResponse {

    private Long postId;
    private Long scrapCount;
}
