package com.example.spot.notification.presentation;

import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.notification.application.ReadNotificationUseCase;
import com.example.spot.notification.presentation.dto.response.NotificationResponseDTO;
import com.example.spot.notification.presentation.dto.response.NotificationResponseDTO.NotificationProcessDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spot")
@RequiredArgsConstructor
public class ReadNotificationController {

    private final ReadNotificationUseCase readNotification;

    //알림 읽음 처리
    @Operation(summary = "[알림 읽음 처리] 일반 알림 읽음 처리", description = """
            ## [알림 읽음 처리] 알림을 읽음 처리합니다.
            알림 처리 후 읽음 처리 결과를 반환합니다.
            """)
    @PostMapping("/notifications/{notificationId}/read")
    public ApiResponse<NotificationProcessDTO> readNotification(@PathVariable Long notificationId) {
        NotificationResponseDTO.NotificationProcessDTO notificationDTO = readNotification.readNotification(
                SecurityUtils.getCurrentUserId(), notificationId);
        return ApiResponse.onSuccess(SuccessStatus._NOTIFICATION_READ, notificationDTO);
    }
}
