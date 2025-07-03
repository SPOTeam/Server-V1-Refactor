package com.example.spot.notification.application;

import com.example.spot.notification.presentation.dto.response.NotificationResponseDTO.NotificationProcessDTO;

public interface HandleAppliedStudyParticipationUseCase {

    // 신청한 스터디 참가
    NotificationProcessDTO joinAppliedStudy(Long studyId, Long memberId, boolean isAccept);
}
