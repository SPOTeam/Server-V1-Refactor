package com.example.spot.notification.application.impl;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.GeneralException;
import com.example.spot.notification.application.GetNotificationUseCase;
import com.example.spot.notification.domain.Notification;
import com.example.spot.notification.domain.NotificationRepository;
import com.example.spot.notification.domain.enums.NotifyType;
import com.example.spot.notification.presentation.dto.response.NotificationResponseDTO.NotificationListDTO;
import com.example.spot.notification.presentation.dto.response.NotificationResponseDTO.NotificationListDTO.NotificationDTO;
import com.example.spot.notification.presentation.dto.response.NotificationResponseDTO.StudyNotificationListDTO;
import com.example.spot.notification.presentation.dto.response.NotificationResponseDTO.StudyNotificationListDTO.StudyNotificationDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetNotificationUseCaseImpl implements GetNotificationUseCase {

    private final NotificationRepository notificationRepository;

    /**
     * 회원이 참가 신청한 스터디에 대한 알림을 조회합니다.
     *
     * @param memberId 사용자 ID
     * @param pageable 페이징 정보
     * @return 참가 신청한 스터디에 대한 알림 목록
     * @throws GeneralException 알림이 존재하지 않는 경우
     */
    @Override
    public StudyNotificationListDTO getAllAppliedStudyNotification(Long memberId, Pageable pageable) {
        // 특정 회원이 참가 신청한 스터디에 대한 알림 조회
        List<Notification> notifications = notificationRepository.findByMemberIdAndTypeAndIsChecked(
                memberId, pageable, NotifyType.STUDY_APPLY, false);

        // 알림이 존재하지 않는 경우
        if (notifications.isEmpty()) {
            throw new GeneralException(ErrorStatus._NOTIFICATION_NOT_FOUND);
        }

        // DTO를 담을 리스트 생성
        List<StudyNotificationDTO> notificationDTOs = new ArrayList<>();

        // 알림을 DTO로 변환하여 리스트에 추가
        notifications.forEach(notification -> {
            StudyNotificationDTO notificationDTO = StudyNotificationDTO.builder()
                    .notificationId(notification.getId())
                    .studyId(notification.getStudy().getId())
                    .createdAt(notification.getCreatedAt())
                    .type(notification.getType())
                    .studyTitle(notification.getStudy().getTitle())
                    .studyProfileImage(notification.getStudy().getProfileImage())
                    .isChecked(notification.getIsChecked())
                    .build();
            notificationDTOs.add(notificationDTO);
        });

        // DTO 리스트를 반환
        return StudyNotificationListDTO.builder()
                .notifications(notificationDTOs)
                .totalNotificationCount((long) notificationDTOs.size())
                .uncheckedNotificationCount((long) (int) notificationDTOs.stream()
                        .filter(notificationDTO -> !notificationDTO.getIsChecked()).count())
                .build();
    }

    /**
     * 회원에게 생성된 알림을 전체 조회합니다.
     *
     * @param memberId 사용자 ID
     * @param pageable 페이징 정보
     * @return 알림 목록
     * @throws GeneralException 알림이 존재하지 않는 경우
     */
    @Override
    public NotificationListDTO getAllNotifications(Long memberId, Pageable pageable) {

        // 특정 회원에게 생성된 알림을 조회
        List<Notification> notifications = notificationRepository.findByMemberIdAndTypeNot(
                memberId, pageable, NotifyType.STUDY_APPLY);

        // 알림이 존재하지 않는 경우
        if (notifications.isEmpty()) {
            throw new GeneralException(ErrorStatus._NOTIFICATION_NOT_FOUND);
        }

        // DTO를 담을 리스트 생성
        List<NotificationDTO> notificationDTOs = new ArrayList<>();

        // 알림을 DTO로 변환하여 리스트에 추가
        notifications.forEach(notification -> {
            NotificationDTO notificationDTO = NotificationDTO.builder()
                    .notificationId(notification.getId())
                    .createdAt(notification.getCreatedAt())
                    .type(notification.getType())
                    .studyId(notification.getStudy().getId())
                    .studyPostId(notification.getStudyPostId())
                    .studyTitle(notification.getStudy().getTitle())
                    .studyProfileImage(notification.getStudy().getProfileImage())
                    .notifierName(notification.getNotifierName()) // 알림 생성한 회원 이름
                    .isChecked(notification.getIsChecked())
                    .build();
            notificationDTOs.add(notificationDTO);
        });

        // DTO 리스트를 반환
        return NotificationListDTO.builder()
                .notifications(notificationDTOs)
                .totalNotificationCount((long) notificationDTOs.size())
                .uncheckedNotificationCount((long) (int) notificationDTOs.stream()
                        .filter(notificationDTO -> !notificationDTO.getIsChecked()).count())
                .build();
    }


}
