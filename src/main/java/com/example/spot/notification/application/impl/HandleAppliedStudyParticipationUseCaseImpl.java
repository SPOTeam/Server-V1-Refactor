package com.example.spot.notification.application.impl;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.GeneralException;
import com.example.spot.notification.application.HandleAppliedStudyParticipationUseCase;
import com.example.spot.notification.domain.Notification;
import com.example.spot.notification.infrastructure.jpa.NotificationRepository;
import com.example.spot.notification.domain.enums.NotifyType;
import com.example.spot.notification.presentation.dto.response.NotificationResponseDTO.NotificationProcessDTO;
import com.example.spot.study.domain.association.StudyMember;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.study.domain.repository.StudyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class HandleAppliedStudyParticipationUseCaseImpl implements HandleAppliedStudyParticipationUseCase {

    private final NotificationRepository notificationRepository;
    private final StudyMemberRepository studyMemberRepository;

    /**
     * 스터디 신청 알림에 대한 처리를 수행합니다. isAccept가 true인 경우 스터디 신청을 수락하고, false인 경우 거절합니다.
     *
     * @param studyId  스터디 ID
     * @param memberId 사용자 ID
     * @param isAccept 스터디 신청 수락 여부
     * @return 스터디 신청 처리 결과 및 처리 일시
     * @throws GeneralException 알림이 존재하지 않거나 이미 읽음 처리된 경우
     * @throws GeneralException 스터디 신청자가 존재하지 않는 경우
     * @see NotificationProcessDTO
     */
    @Override
    public NotificationProcessDTO joinAppliedStudy(Long studyId, Long memberId, boolean isAccept) {

        // 스터디 신청 알림 조회 -
        Notification notification = notificationRepository.findByMemberIdAndStudyIdAndTypeAndIsChecked(
                        memberId, studyId, NotifyType.STUDY_APPLY, false)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOTIFICATION_NOT_FOUND));

        // 이미 읽음 처리된 경우
        if (notification.getIsChecked()) {
            throw new GeneralException(ErrorStatus._NOTIFICATION_ALREADY_READ);
        }

        // 스터디 신청자 조회
        StudyMember studyMember = studyMemberRepository.findByMemberIdAndStudyIdAndStatus(
                memberId, studyId, StudyApplicationStatus.AWAITING_SELF_APPROVAL).orElseThrow(
                () -> new GeneralException(ErrorStatus._STUDY_APPLICANT_NOT_FOUND));

        // 스터디 신청 처리
        if (isAccept) {
            // 스터디 신청 수락
            studyMember.setStatus(StudyApplicationStatus.APPROVED);
        } else {
            // 스터디 신청 거절
            studyMember.setStatus(StudyApplicationStatus.REJECTED);
        }
        // 알림 읽음 처리
        notification.markAsRead();

        // 스터디 신청 처리 결과 반환
        return NotificationProcessDTO.builder()
                .isAccept(isAccept)
                .processedAt(notification.getUpdatedAt())
                .build();
    }
}
