package com.example.spot.study.application;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.GeneralException;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.common.api.exception.handler.StudyHandler;
import com.example.spot.common.application.s3.S3ImageService;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.member.domain.Member;
import com.example.spot.member.domain.enums.Status;
import com.example.spot.member.infrastructure.MemberRepository;
import com.example.spot.notification.domain.Notification;
import com.example.spot.notification.domain.NotificationRepository;
import com.example.spot.notification.domain.enums.NotifyType;
import com.example.spot.report.domain.MemberReportRepository;
import com.example.spot.report.domain.StoryReportRepository;
import com.example.spot.story.infrastructure.repository.StoryRepository;
import com.example.spot.study.domain.Study;
import com.example.spot.study.domain.StudyRepository;
import com.example.spot.study.domain.association.StudyMember;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.study.domain.repository.StudyMemberRepository;
import com.example.spot.study.presentation.dto.request.StudyMemberRequestDTO;
import com.example.spot.study.presentation.dto.response.StudyMemberResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class StudyMemberCommandServiceImpl implements StudyMemberCommandService {

    private final MemberRepository memberRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final StudyRepository studyRepository;
    private final StoryRepository storyRepository;
    private final NotificationRepository notificationRepository;
    private final MemberReportRepository memberReportRepository;
    private final StoryReportRepository storyReportRepository;


    // S3 Service
    private final S3ImageService s3ImageService;

    /* ----------------------------- 진행중인 스터디 관련 API ------------------------------------- */

    /**
     * 진행중인 스터디에서 탈퇴하기 위한 메서드입니다. 스터디장은 스터디를 탈퇴할 수 없으며 스터디를 종료하고자 하는 경우 스터디 terminateStudy API를 호출해야 합니다.
     *
     * @param studyId 타겟 회원이 탈퇴하고자 하는 스터디의 아이디를 입력 받습니다.
     * @return 탈퇴한 스터디의 아이디와 이름, 탈퇴한 회원의 아이디와 이름이 반환됩니다.
     */
    public StudyMemberResponseDTO.WithdrawalDTO withdrawFromStudy(Long studyId) {

        // Authorization
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        StudyMember studyMember = studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId,
                        StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 참여가 승인되지 않은 스터디는 탈퇴할 수 없음
        if (!studyMember.getStatus().equals(StudyApplicationStatus.APPROVED)) {
            throw new StudyHandler(ErrorStatus._STUDY_NOT_APPROVED);
        }
        // 스터디장은 스터디를 탈퇴할 수 없음
        if (studyMember.getIsOwned()) {
            throw new StudyHandler(ErrorStatus._STUDY_OWNER_CANNOT_WITHDRAW);
        }

        studyMemberRepository.delete(studyMember);

        return StudyMemberResponseDTO.WithdrawalDTO.toDTO(member, study);
    }

    @Override
    public StudyMemberResponseDTO.WithdrawalDTO withdrawHostFromStudy(Long studyId,
                                                                      StudyMemberRequestDTO.HostWithdrawDTO hostWithdrawDTO) {
        // Authorization
        Long hostId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(hostId);

        StudyMember studyMember = studyMemberRepository.findByMemberIdAndStudyIdAndStatus(hostId, studyId,
                        StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        if (!studyMember.getIsOwned()) {
            throw new StudyHandler(ErrorStatus._STUDY_OWNER_ONLY_CAN_WITHDRAW);
        }

        StudyMember newHostStudy = studyMemberRepository.findByMemberIdAndStudyIdAndStatus(
                        hostWithdrawDTO.getNewHostId(), studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_EXIST));

        studyMemberRepository.delete(studyMember);

        newHostStudy.setIsOwned(true);
        newHostStudy.setReason(hostWithdrawDTO.getReason());

        studyMemberRepository.save(newHostStudy);

        return StudyMemberResponseDTO.WithdrawalDTO.toDTO(newHostStudy.getMember(), newHostStudy.getStudy());
    }

    /**
     * 운영중인 스터디를 종료하는 메서드입니다. 스터디장만 호출 가능합니다.
     *
     * @param studyId     종료할 스터디의 아이디를 입력 받습니다.
     * @param performance 종료할 스터디의 성과를 입력 받습니다.
     * @return 종료된 스터디의 아이디, 이름, 상태를 반환합니다.
     */
    public StudyResponseDTO.TerminationDTO terminateStudy(Long studyId, String performance) {

        // Authorization
        Long memberId = SecurityUtils.getCurrentUserId();
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        StudyMember studyMember = studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId,
                        StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 스터디장이 아니면 스터디를 종료할 수 없음
        if (studyMember.getIsOwned().equals(false)) {
            throw new StudyHandler(ErrorStatus._STUDY_OWNER_ONLY_CAN_TERMINATE);
        }

        // 이미 종료된 스터디는 종료할 수 없음
        if (study.getStatus().equals(Status.OFF)) {
            throw new StudyHandler(ErrorStatus._STUDY_ALREADY_TERMINATED);
        }

        study.terminateStudy(performance);
        studyRepository.save(study);

        return StudyResponseDTO.TerminationDTO.toDTO(study);
    }

    /**
     * 스터디 신청을 처리합니다. isAccept가 true이면 승인, false이면 거절합니다. 이후 관련 알림을 생성합니다. 알림을 통해 최종 참여 승인을 해야 스터디에 참여할 수 있습니다.
     *
     * @param memberId 스터디에 신청한 회원 ID
     * @param studyId  스터디 ID
     * @param isAccept 승인 여부
     * @return 스터디 신청 처리 결과 및 처리 시간
     * @throws GeneralException 스터디 신청을 처리하는 회원이 스터디 소유자가 아닐 때
     * @throws GeneralException 스터디 소유자가 신청한 경우
     * @throws StudyHandler     스터디 신청자를 찾을 수 없을 때
     * @throws StudyHandler     스터디 신청이 이미 처리되었을 때
     * @throws MemberHandler    스터디 장을 찾을 수 없을 때
     */
    @Override
    public StudyMemberResponseDTO.ApplicationStatusDTO acceptAndRejectStudyApply(Long memberId, Long studyId,
                                                                                 boolean isAccept) {

        // 신청을 처리하는 회원이 스터디 소유자인지 확인
        if (!isOwner(SecurityUtils.getCurrentUserId(), studyId)) {
            throw new GeneralException(ErrorStatus._ONLY_STUDY_OWNER_CAN_ACCESS_APPLICANTS);
        }

        // 스터디 신청자 조회
        StudyMember studyMember = studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId,
                        StudyApplicationStatus.APPLIED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_APPLICANT_NOT_FOUND));

        // 스터디 소유자가 스터디 신청한 경우
        if (studyMember.getIsOwned()) {
            throw new GeneralException(ErrorStatus._STUDY_OWNER_CANNOT_APPLY);
        }

        // 스터디 신청이 이미 처리되었을 때
        if (studyMember.getStatus() != StudyApplicationStatus.APPLIED) {
            throw new GeneralException(ErrorStatus._STUDY_APPLY_ALREADY_PROCESSED);
        }

        // 스터디 장 조회
        Member owner = memberRepository.findById(SecurityUtils.getCurrentUserId())
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        // 승인인 경우
        if (isAccept) {
            // 스터디 참여 승인 최종 대기
            studyMember.setStatus(StudyApplicationStatus.AWAITING_SELF_APPROVAL);

            // 알림 생성
            Notification notification = Notification.builder()
                    .member(studyMember.getMember()) // 신청자
                    .study(studyMember.getStudy())
                    .notifierName(owner.getName()) // 스터디장 이름
                    .type(NotifyType.STUDY_APPLY)
                    .isChecked(Boolean.FALSE)
                    .build();

            notificationRepository.save(notification);
        } else { // 거절인 경우
            studyMember.setStatus(StudyApplicationStatus.REJECTED);
            studyMemberRepository.delete(studyMember);
        }

        // 스터디 신청 처리 결과 반환
        return StudyMemberResponseDTO.ApplicationStatusDTO.builder()
                .status(studyMember.getStatus())
                .updatedAt(studyMember.getUpdatedAt())
                .build();
    }

    /**
     * 스터디 신청을 처리합니다. isAccept가 true이면 승인, false이면 거절합니다. 이 메서드를 사용하면 알림 처리 없이 바로 스터디에 참여할 수 있습니다.
     *
     * @param memberId 스터디에 신청한 회원 ID
     * @param studyId  스터디 ID
     * @param isAccept 승인 여부
     * @return 스터디 신청 처리 결과 및 처리 시간
     * @throws GeneralException 스터디 신청을 처리하는 회원이 스터디 소유자가 아닐 때
     * @throws GeneralException 스터디 소유자가 신청한 경우
     * @throws StudyHandler     스터디 신청자를 찾을 수 없을 때
     * @throws StudyHandler     스터디 신청이 이미 처리되었을 때
     * @throws MemberHandler    스터디 장을 찾을 수 없을 때
     */
    @Override
    public StudyMemberResponseDTO.ApplicationStatusDTO acceptAndRejectStudyApplyForTest(Long memberId, Long studyId,
                                                                                        boolean isAccept) {

        // 스터디 신청을 처리하는 회원이 스터디 소유자인지 확인
        if (!isOwner(SecurityUtils.getCurrentUserId(), studyId)) {
            throw new GeneralException(ErrorStatus._ONLY_STUDY_OWNER_CAN_ACCESS_APPLICANTS);
        }

        // 스터디 신청자 조회
        StudyMember studyMember = studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId,
                        StudyApplicationStatus.APPLIED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_APPLICANT_NOT_FOUND));

        // 스터디 소유자가 스터디 신청한 경우
        if (studyMember.getIsOwned()) {
            throw new GeneralException(ErrorStatus._STUDY_OWNER_CANNOT_APPLY);
        }

        // 스터디 신청이 이미 처리되었을 때
        if (studyMember.getStatus() != StudyApplicationStatus.APPLIED) {
            throw new GeneralException(ErrorStatus._STUDY_APPLY_ALREADY_PROCESSED);
        }

        // 스터디 장 조회
        Member owner = memberRepository.findById(SecurityUtils.getCurrentUserId())
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        // 승인인 경우
        if (isAccept) {
            studyMember.setStatus(StudyApplicationStatus.APPROVED);
        } else {
            studyMember.setStatus(StudyApplicationStatus.REJECTED);
            studyMemberRepository.delete(studyMember);
        }

        // 스터디 신청 처리 결과 반환
        return StudyMemberResponseDTO.ApplicationStatusDTO.builder()
                .status(studyMember.getStatus())
                .updatedAt(studyMember.getUpdatedAt())
                .build();
    }

    /**
     * 회원이 스터디 장인지 확인합니다.
     *
     * @param memberId 확인 하려는 회원 ID
     * @param studyId  확인 하려는 스터디 ID
     * @return 스터디 장 여부를 반환합니다.
     */
    private boolean isOwner(Long memberId, Long studyId) {
        return studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(memberId, studyId, true).isPresent();
    }
}
