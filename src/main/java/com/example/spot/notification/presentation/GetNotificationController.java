package com.example.spot.notification.presentation;

import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.notification.application.GetNotificationUseCase;
import com.example.spot.notification.presentation.dto.response.NotificationResponseDTO.NotificationListDTO;
import com.example.spot.notification.presentation.dto.response.NotificationResponseDTO.StudyNotificationListDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spot")
@RequiredArgsConstructor
public class GetNotificationController {

    private final GetNotificationUseCase getNotificationUseCase;

    //알림 전체 조회
    @Operation(summary = "[내 알림 전체 조회] 내게 생성된 알림을 전체 조회합니다.", description = """
            ## [내 알림 전체 조회] 내게 생성된 알림을 전체 조회합니다.
            
            알림의 내용, 생성 시간, 알림의 종류, 알림을 생성한 스터디의 이름을 반환합니다.
            
            알림의 종류는 다음과 같습니다.
            ANNOUNCEMENT, SCHEDULE_UPDATE ,TO_DO_UPDATE, POPULAR_POST
            
            """)
    @GetMapping("/notifications")
    public ApiResponse<NotificationListDTO> getAllNotifications(
            @RequestParam @Min(0) Integer page,
            @RequestParam @Min(1) Integer size
    ) {
        NotificationListDTO notificationDTO = getNotificationUseCase.getAllNotifications(
                SecurityUtils.getCurrentUserId(), PageRequest.of(page, size));
        return ApiResponse.onSuccess(SuccessStatus._NOTIFICATION_FOUND, notificationDTO);
    }


    @Operation(summary = "[참가 신청한 스터디 알림 조회] 참가 신청한 스터디에 대한 알림 조회", description = """
            
            ## [참가 신청한 스터디 알림 조회] 회원이 참가 신청한 스터디에 대한 알림을 조회합니다.
            
            참가 신청 했던 스터디의 제목과 프로필 이미지를 반환합니다.
            해당 알림은 본인의 스터디 신청을 스터디 소유자가 승인 처리 했을 경우 생성됩니다. 
            *참가 신청한 스터디 알람 처리* API를 통해 참여 버튼을 누르면 스터디에 참여할 수 있습니다.
            """)
    @GetMapping("/notifications/applied-study")
    public ApiResponse<StudyNotificationListDTO> getAppliedStudyNotification(
            @RequestParam @Min(0) Integer page,
            @RequestParam @Min(1) Integer size
    ) {
        StudyNotificationListDTO notificationDTO = getNotificationUseCase.getAllAppliedStudyNotification(
                SecurityUtils.getCurrentUserId(), PageRequest.of(page, size));
        return ApiResponse.onSuccess(SuccessStatus._NOTIFICATION_FOUND, notificationDTO);
    }
}
