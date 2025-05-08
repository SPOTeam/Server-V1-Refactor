package com.example.spot.post.presentation.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import com.example.spot.post.presentation.dto.PostBest5DetailResponse;

@Builder
@Getter
@AllArgsConstructor
public class PostAnnouncementResponse {
    private List<PostBest5DetailResponse> responses;

}
