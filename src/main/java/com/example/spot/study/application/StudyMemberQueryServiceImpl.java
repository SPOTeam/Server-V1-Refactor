package com.example.spot.study.application;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.GeneralException;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.common.api.exception.handler.StudyHandler;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.member.domain.Member;
import com.example.spot.member.infrastructure.jpa.MemberRepository;
import com.example.spot.story.domain.StoryRepository;
import com.example.spot.study.domain.StudyRepository;
import com.example.spot.study.domain.association.StudyMember;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.study.domain.repository.StudyMemberRepository;
import com.example.spot.study.presentation.dto.response.StudyMemberResponseDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyMemberQueryServiceImpl implements StudyMemberQueryService {

    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;
    private final StoryRepository storyRepository;
    private final StudyMemberRepository studyMemberRepository;

    /**
     * 특정 스터디의 회원 목록을 전체 조회 합니다. 가입된 스터디가 아니더라도 회원 목록을 조회할 수 있습니다.
     *
     * @param studyId 스터디 ID
     * @return 스터디에 참여하는 회원 목록을 반환합니다.
     * @throws GeneralException 스터디 할 일이 존재하지 않는 경우
     * @throws GeneralException 스터디 멤버가 아닌 경우
     */
    @Override
    public StudyMemberResponseDTO.StudyMemberListDTO findStudyMembers(Long studyId) {

        // 스터디 멤버 조회
        List<StudyMember> memberStudies = studyMemberRepository.findAllByStudyIdAndStatus(studyId,
                StudyApplicationStatus.APPROVED);

        // 스터디 멤버가 존재하지 않는 경우
        if (memberStudies.isEmpty()) {
            throw new GeneralException(ErrorStatus._STUDY_MEMBER_NOT_FOUND);
        }

        // DTO로 변환하여 반환
        List<StudyMemberResponseDTO.StudyMemberDTO> memberDTOS = memberStudies.stream()
                .map(memberStudy -> StudyMemberResponseDTO.StudyMemberDTO.builder()
                        .memberId(memberStudy.getMember().getId())
                        .nickname(memberStudy.getMember().getName())
                        .profileImage(memberStudy.getMember().getProfileImage())
                        .build()).toList();
        // DTO로 변환하여 반환
        return new StudyMemberResponseDTO.StudyMemberListDTO(memberDTOS);
    }


    /**
     * 회원이 모집중인 스터디에 신청한 회원 목록을 불러옵니다.
     *
     * @param studyId 스터디 ID
     * @return 스터디 신청자 목록을 반환합니다.
     * @throws GeneralException 스터디 신청자가 존재 하지 않는 경우
     * @throws GeneralException 조회 하는 회원이 스터디 장이 아닌 경우
     */
    @Override
    public StudyMemberResponseDTO.StudyMemberListDTO findStudyApplicants(Long studyId) {

        // 로그인한 회원이 해당 스터디 장인지 확인
        if (!isOwner(SecurityUtils.getCurrentUserId(), studyId)) {
            throw new GeneralException(ErrorStatus._ONLY_STUDY_OWNER_CAN_ACCESS_APPLICANTS);
        }

        // 스터디 신청자 조회
        List<StudyMember> memberStudies = studyMemberRepository.findAllByStudyIdAndStatus(studyId,
                StudyApplicationStatus.APPLIED);

        // 스터디 신청자가 존재하지 않는 경우
        if (memberStudies.isEmpty()) {
            throw new GeneralException(ErrorStatus._STUDY_APPLICANT_NOT_FOUND);
        }

        // DTO로 변환하여 반환
        List<StudyMemberResponseDTO.StudyMemberDTO> memberDTOS = memberStudies.stream()
                .map(memberStudy -> StudyMemberResponseDTO.StudyMemberDTO.builder()
                        .memberId(memberStudy.getMember().getId())
                        .nickname(memberStudy.getMember().getName())
                        .profileImage(memberStudy.getMember().getProfileImage())
                        .build()).toList();

        // DTO로 변환하여 반환
        return new StudyMemberResponseDTO.StudyMemberListDTO(memberDTOS);
    }

    @Override
    public StudyMemberResponseDTO.HostDTO getStudyHost(Long studyId) {

        // Authorization
        Long memberId = SecurityUtils.getCurrentUserId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        // 스터디 호스트 찾기
        StudyMember studyHost = studyMemberRepository.findByStudyIdAndIsOwned(studyId, true)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_OWNER_NOT_FOUND));

        // 로그인한 회원이 호스트인지 확인
        if (studyHost.getMember().getId().equals(memberId)) {
            return StudyMemberResponseDTO.HostDTO.toDTO(true, member);
        } else {
            return StudyMemberResponseDTO.HostDTO.toDTO(false, studyHost.getMember());
        }
    }

    /**
     * 스터디 신청자의 정보를 조회합니다.
     *
     * @param studyId  스터디 ID
     * @param memberId 회원 ID
     * @return 스터디 신청자 정보를 반환합니다.
     * @throws GeneralException 스터디 신청자가 존재하지 않는 경우
     * @throws GeneralException 조회 하는 회원이 스터디 장이 아닌 경우
     * @throws GeneralException 스터디 장은 스터디에 신청할 수 없음
     */
    @Override
    public StudyMemberResponseDTO.ApplyingMemberDTO findStudyApplication(Long studyId, Long memberId) {

        // 로그인한 회원이 해당 스터디 장인지 확인
        if (!isOwner(SecurityUtils.getCurrentUserId(), studyId)) {
            throw new GeneralException(ErrorStatus._ONLY_STUDY_OWNER_CAN_ACCESS_APPLICANTS);
        }

        // 스터디 신청자 조회
        StudyMember studyMember = studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId,
                        StudyApplicationStatus.APPLIED)
                .orElseThrow(() -> new GeneralException(ErrorStatus._STUDY_APPLICANT_NOT_FOUND));

        // 스터디 장은 스터디에 신청할 수 없음
        if (studyMember.getIsOwned()) {
            throw new GeneralException(ErrorStatus._STUDY_OWNER_CANNOT_APPLY);
        }

        // DTO로 변환하여 반환
        return StudyMemberResponseDTO.ApplyingMemberDTO.builder()
                .memberId(studyMember.getMember().getId())
                .studyId(studyMember.getStudy().getId())
                .introduction(studyMember.getIntroduction())
                .nickname(studyMember.getMember().getName())
                .profileImage(studyMember.getMember().getProfileImage())
                .build();
    }

    /**
     * 해당 스터디 신청 여부를 조회합니다. true: 신청, false: 미신청
     *
     * @param studyId 스터디 ID
     * @return 신청 여부와 스터디 ID를 반환합니다.
     * @throws GeneralException 이미 스터디 멤버인 경우
     */
    @Override
    public StudyMemberResponseDTO.AppliedStudyDTO isApplied(Long studyId) {
        // 로그인한 회원 ID 조회
        Long currentUserId = SecurityUtils.getCurrentUserId();

        // 이미 스터디 멤버인 경우
        if (isMember(currentUserId, studyId)) {
            throw new GeneralException(ErrorStatus._ALREADY_STUDY_MEMBER);
        }

        // DTO로 변환하여 반환
        return StudyMemberResponseDTO.AppliedStudyDTO.builder()
                .isApplied(studyMemberRepository.existsByMemberIdAndStudyIdAndStatus(currentUserId, studyId,
                        StudyApplicationStatus.APPLIED))
                .studyId(studyId)
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

    /**
     * 회원이 스터디 구성원인지 확인합니다.
     *
     * @param memberId 확인 하려는 회원 ID
     * @param studyId  확인 하려는 스터디 ID
     * @return 스터디 참여 여부를 반환합니다.
     */
    private boolean isMember(Long memberId, Long studyId) {
        return studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId,
                StudyApplicationStatus.APPROVED).isPresent();
    }

}
