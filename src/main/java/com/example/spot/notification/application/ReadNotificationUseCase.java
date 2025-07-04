package com.example.spot.notification.application;

import com.example.spot.notification.presentation.dto.response.NotificationResponseDTO.NotificationProcessDTO;

public interface ReadNotificationUseCase {

    // 알림 읽음 처리
    NotificationProcessDTO readNotification(Long memberId, Long notificationId);
}
