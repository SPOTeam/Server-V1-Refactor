package com.example.spot.study.presentation.dto.response;

import com.example.spot.study.domain.enums.StudyApplicationStatus;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudyApplyResponseDTO {
    private StudyApplicationStatus status;
    private LocalDateTime updatedAt;

}
