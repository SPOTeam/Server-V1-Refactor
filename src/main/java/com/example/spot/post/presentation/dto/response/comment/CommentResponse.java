package com.example.spot.post.presentation.dto.response.comment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResponse {

    private List<CommentDetailResponse> comments;


}