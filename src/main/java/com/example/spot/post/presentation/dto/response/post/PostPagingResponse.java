package com.example.spot.post.presentation.dto.response.post;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PostPagingResponse {

    private String postType;
    private List<PostPagingDetailResponse> postResponses;
    private Integer totalPage;
    private Long totalElements;
    private Boolean isFirst;
    private Boolean isLast;

}
