package com.example.spot.post.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class PostAnnouncementResponse {
    private List<PostBest5DetailResponse> responses;

}
