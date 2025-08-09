package com.example.spot.service.notification;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.example.spot.common.api.exception.GeneralException;
import com.example.spot.member.domain.Member;
import com.example.spot.notification.application.impl.HandleAppliedStudyParticipationUseCaseImpl;
import com.example.spot.notification.application.impl.ReadNotificationUseCaseImpl;
import com.example.spot.notification.domain.Notification;
import com.example.spot.notification.infrastructure.jpa.NotificationRepository;
import com.example.spot.notification.domain.enums.NotifyType;
import com.example.spot.notification.presentation.dto.response.NotificationResponseDTO.NotificationProcessDTO;
import com.example.spot.study.domain.Study;
import com.example.spot.study.domain.association.StudyMember;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.study.domain.repository.StudyMemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class NotificationCommandServiceTest {

    @InjectMocks
    private ReadNotificationUseCaseImpl readNotificationUseCase;

    @InjectMocks
    private HandleAppliedStudyParticipationUseCaseImpl handleAppliedStudyParticipationUseCase;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private StudyMemberRepository studyMemberRepository;

    @Mock
    private Member member;

    @Mock
    private Study study;

    private Notification notification1;
    private Notification notification2;
    private Notification notification3;

    private StudyMember studyMember;

    @BeforeEach
    void init() {
        this.notification1 = Notification.builder()
                .id(1L).study(study).member(member).type(NotifyType.STUDY_APPLY).notifierName("Test")
                .isChecked(false).build();
        this.notification2 = Notification.builder()
                .id(2L).study(study).member(member).type(NotifyType.ANNOUNCEMENT).notifierName("Test")
                .isChecked(false).build();
        this.notification2 = Notification.builder()
                .id(3L).study(study).member(member).type(NotifyType.STUDY_APPLY).notifierName("Test")
                .isChecked(true).build();

        this.studyMember = StudyMember.builder().status(StudyApplicationStatus.APPLIED).build();
    }

    /* --------------------------------- 알림 읽음 처리 ----------------------------------- */

    @Test
    @DisplayName("알림 읽음 처리 성공")
    void 알림_읽음_처리_성공() {
        // given
        given(member.getId()).willReturn(1L);

        when(notificationRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(notification1));

        // when
        NotificationProcessDTO response = readNotificationUseCase.readNotification(1L, 1L);

        // then
        assertEquals(true, response.isAccept());
    }

    @Test
    @DisplayName("알림이 없는 경우")
    void 알림이_없는_경우() {
        // given
        when(notificationRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(GeneralException.class, () -> {
            readNotificationUseCase.readNotification(1L, 1L);
        });
    }

    @Test
    @DisplayName("알림이 사용자에게 속하지 않는 경우")
    void 알림이_사용자에게_속하지_않는_경우() {
        // given
        given(member.getId()).willReturn(1L);

        when(notificationRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(notification1));

        // when & then
        assertThrows(GeneralException.class, () -> {
            readNotificationUseCase.readNotification(2L, 1L);
        });
    }

    @Test
    @DisplayName("이미 읽음 처리된 경우")
    void 이미_읽음_처리_된_경우() {
        // given
        given(member.getId()).willReturn(1L);

        when(notificationRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(notification3));

        // when & then
        assertThrows(GeneralException.class, () -> {
            readNotificationUseCase.readNotification(1L, 1L);
        });
    }

    /* --------------------------------- 스터디 알림 처리 ----------------------------------- */

    @Test
    @DisplayName("스터디 신청 처리 성공")
    void 스터디_신청_처리_성공() {
        // given
        when(notificationRepository.findByMemberIdAndStudyIdAndTypeAndIsChecked(
                anyLong(), anyLong(), any(), anyBoolean()
        )).thenReturn(Optional.ofNullable(notification1));

        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(
                anyLong(), anyLong(), any()
        )).thenReturn(Optional.ofNullable(studyMember));

        // when
        NotificationProcessDTO response =
                handleAppliedStudyParticipationUseCase.joinAppliedStudy(1L, 1L, true);

        // then
        assertEquals(true, response.isAccept());
    }

    @Test
    @DisplayName("스터디 신청 알림이 이미 처리 된 경우")
    void 스터디_알림이_이미_읽음_처리_된_경우() {
        // given
        when(notificationRepository.findByMemberIdAndStudyIdAndTypeAndIsChecked(
                anyLong(), anyLong(), any(), anyBoolean()
        )).thenReturn(Optional.ofNullable(notification3));

        // when & then
        assertThrows(GeneralException.class, () -> {
            handleAppliedStudyParticipationUseCase.joinAppliedStudy(1L, 1L, true);
        });
    }

    @Test
    @DisplayName("스터디 신청 알림이 없는 경우")
    void 스터디_신청이_없는_경우() {
        // given
        when(notificationRepository.findByMemberIdAndStudyIdAndTypeAndIsChecked(
                anyLong(), anyLong(), any(), anyBoolean()
        )).thenReturn(Optional.ofNullable(notification1));

        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(
                anyLong(), anyLong(), any()
        )).thenReturn(Optional.empty());

        // when & then
        assertThrows(GeneralException.class, () -> {
            handleAppliedStudyParticipationUseCase.joinAppliedStudy(1L, 1L, true);
        });
    }
}
