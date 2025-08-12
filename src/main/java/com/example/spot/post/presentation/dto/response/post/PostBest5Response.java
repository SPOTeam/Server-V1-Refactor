package com.example.spot.post.presentation.dto.response.post;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostBest5Response {

    private String sortType;
    private List<PostBest5DetailResponse> postBest5Responses;

}
