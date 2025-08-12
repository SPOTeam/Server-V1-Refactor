package com.example.spot.post.presentation.dto.response.post;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PostRepresentativeResponse {

    private List<PostRepresentativeDetailResponse> responses;

}
