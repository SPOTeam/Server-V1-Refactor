package com.example.spot.legacy.web.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class PostRepresentativeResponse {
    private List<PostRepresentativeDetailResponse> responses;

}
