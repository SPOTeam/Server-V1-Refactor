package com.example.spot.study.presentation.dto.response;

import com.example.spot.study.domain.enums.StudyLikeStatus;
import com.example.spot.member.domain.association.PreferredStudy;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudyLikeResponseDTO {
    private String studyTitle;
    private LocalDateTime createdAt;
    private StudyLikeStatus status;

    public StudyLikeResponseDTO(PreferredStudy preferredStudy){
        this.studyTitle = preferredStudy.getStudy().getTitle();
        this.createdAt = preferredStudy.getCreatedAt();
        this.status = preferredStudy.getStudyLikeStatus();
    }
}
