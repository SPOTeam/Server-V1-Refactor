package com.example.spot.notification.application;

import com.example.spot.notification.presentation.dto.response.NotificationResponseDTO.NotificationListDTO;
import com.example.spot.notification.presentation.dto.response.NotificationResponseDTO.StudyNotificationListDTO;
import org.springframework.data.domain.Pageable;

public interface GetNotificationUseCase {

    // 생성된 알림 전체 조회
    NotificationListDTO getAllNotifications(Long memberId, Pageable pageable);

    // 신청한 스터디 알림 전체 조회
    StudyNotificationListDTO getAllAppliedStudyNotification(Long memberId, Pageable pageable);
}
