package com.example.spot.post.presentation.dto.response.post;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Getter
@AllArgsConstructor
public class ScrapsPostDeleteResponse {

    private List<ScrapPostResponse> cancelScraps;
}
