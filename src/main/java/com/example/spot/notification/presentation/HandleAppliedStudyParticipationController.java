package com.example.spot.notification.presentation;

import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.notification.application.HandleAppliedStudyParticipationUseCase;
import com.example.spot.notification.presentation.dto.response.NotificationResponseDTO.NotificationProcessDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spot")
@RequiredArgsConstructor
public class HandleAppliedStudyParticipationController {

    private final HandleAppliedStudyParticipationUseCase handleAppliedStudyParticipationUseCase;

    // 신청한 스터디 참여
    @Operation(summary = "[참가 신청한 스터디 알람 처리] 알림 통한 스터디 참여 처리", description = """
            ## [참가 신청한 스터디 알람 처리] 유저가 참가 신청한 스터디에 대해 생성된 알림을 처리합니다.
            isAccepted 값이 true일 경우 스터디 참여, false일 경우 스터디 참여 거절 처리합니다.
            """)
    @PostMapping("/notifications/applied-study/{studyId}/join")
    public ApiResponse<NotificationProcessDTO> joinAppliedStudy(
            @PathVariable Long studyId,
            @RequestParam boolean isAccepted) {
        NotificationProcessDTO notification = handleAppliedStudyParticipationUseCase.joinAppliedStudy(
                studyId, SecurityUtils.getCurrentUserId(), isAccepted);
        return ApiResponse.onSuccess(SuccessStatus._NOTIFICATION_APPLIED_STUDY_JOINED, notification);
    }
}
