package com.example.spot.notification.application.notification;

import com.example.spot.notification.presentation.dto.notification.NotificationResponseDTO.NotificationProcessDTO;

public interface NotificationCommandService {

    // 알림 읽음 처리
    NotificationProcessDTO readNotification(Long memberId, Long notificationId);

    // 신청한 스터디 참가
    NotificationProcessDTO joinAppliedStudy(Long studyId, Long memberId, boolean isAccept);



}
