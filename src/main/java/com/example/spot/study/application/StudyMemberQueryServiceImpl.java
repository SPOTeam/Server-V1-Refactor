package com.example.spot.study.application;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.common.api.exception.handler.StudyHandler;
import com.example.spot.member.domain.Member;
import com.example.spot.story.domain.Story;
import com.example.spot.study.domain.association.StudyMember;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.member.domain.MemberRepository;
import com.example.spot.study.domain.repository.StudyMemberRepository;
import com.example.spot.story.domain.StoryRepository;
import com.example.spot.study.domain.StudyRepository;
import com.example.spot.study.presentation.dto.response.StudyImageResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyMemberResDTO;
import com.example.spot.study.presentation.dto.response.StudyMemberResponseDTO;
import com.example.spot.story.web.dto.response.StoryResponseDTO;

import com.example.spot.common.security.utils.SecurityUtils;

import com.example.spot.study.presentation.dto.response.StudyMemberResponseDTO.StudyApplicantDTO;
import lombok.RequiredArgsConstructor;
import com.example.spot.common.api.exception.GeneralException;
import com.example.spot.study.presentation.dto.response.StudyMemberResponseDTO.StudyApplyMemberDTO;
import com.example.spot.study.presentation.dto.response.StudyMemberResponseDTO.StudyMemberDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyMemberQueryServiceImpl implements StudyMemberQueryService {

    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;
    private final StoryRepository storyRepository;
    private final StudyMemberRepository studyMemberRepository;


    /**
     * 스터디 최근 공지사항을 1개 조회합니다.
     * @param studyId 스터디 ID
     * @return 제목과 내용을 반환합니다.
     * @throws GeneralException 스터디 공지사항이 존재하지 않는 경우
     * @throws GeneralException 스터디 멤버가 아닌 경우
     */
    @Override
    public StoryResponseDTO findStudyAnnouncementPost(Long studyId) {

        // 로그인한 회원이 해당 스터디 회원인지 확인
        if (!isMember(SecurityUtils.getCurrentUserId(), studyId))
            throw new GeneralException(ErrorStatus._ONLY_STUDY_MEMBER_CAN_ACCESS_ANNOUNCEMENT_POST);

        // 스터디 공지사항 조회
        Story story = storyRepository.findByStudyIdAndIsAnnouncement(
            studyId, true).orElseThrow(() -> new GeneralException(ErrorStatus._STUDY_POST_NOT_FOUND));

        // DTO로 변환하여 반환
        return StoryResponseDTO.builder()
            .title(story.getTitle())
            .content(story.getContent()).build();
    }

    /**
     * 특정 스터디의 회원 목록을 전체 조회 합니다. 가입된 스터디가 아니더라도 회원 목록을 조회할 수 있습니다.
     * @param studyId 스터디 ID
     * @return 스터디에 참여하는 회원 목록을 반환합니다.
     * @throws GeneralException 스터디 할 일이 존재하지 않는 경우
     * @throws GeneralException 스터디 멤버가 아닌 경우
     */
    @Override
    public StudyMemberResponseDTO findStudyMembers(Long studyId) {

        // 스터디 멤버 조회
        List<StudyMember> memberStudies = studyMemberRepository.findAllByStudyIdAndStatus(studyId, StudyApplicationStatus.APPROVED);

        // 스터디 멤버가 존재하지 않는 경우
        if (memberStudies.isEmpty())
            throw new GeneralException(ErrorStatus._STUDY_MEMBER_NOT_FOUND);

        // DTO로 변환하여 반환
        List<StudyMemberDTO> memberDTOS = memberStudies.stream().map(memberStudy -> StudyMemberDTO.builder()
            .memberId(memberStudy.getMember().getId())
            .nickname(memberStudy.getMember().getName())
            .profileImage(memberStudy.getMember().getProfileImage())
            .build()).toList();
        // DTO로 변환하여 반환
        return new StudyMemberResponseDTO(memberDTOS);
    }


    /**
     * 회원이 모집중인 스터디에 신청한 회원 목록을 불러옵니다.
     * @param studyId 스터디 ID
     * @return 스터디 신청자 목록을 반환합니다.
     * @throws GeneralException 스터디 신청자가 존재 하지 않는 경우
     * @throws GeneralException 조회 하는 회원이 스터디 장이 아닌 경우
     */
    @Override
    public StudyMemberResponseDTO findStudyApplicants(Long studyId) {

        // 로그인한 회원이 해당 스터디 장인지 확인
        if (!isOwner(SecurityUtils.getCurrentUserId(), studyId))
            throw new GeneralException(ErrorStatus._ONLY_STUDY_OWNER_CAN_ACCESS_APPLICANTS);

        // 스터디 신청자 조회
        List<StudyMember> memberStudies = studyMemberRepository.findAllByStudyIdAndStatus(studyId, StudyApplicationStatus.APPLIED);

        // 스터디 신청자가 존재하지 않는 경우
        if (memberStudies.isEmpty())
            throw new GeneralException(ErrorStatus._STUDY_APPLICANT_NOT_FOUND);

        // DTO로 변환하여 반환
        List<StudyMemberDTO> memberDTOS = memberStudies.stream().map(memberStudy -> StudyMemberDTO.builder()
            .memberId(memberStudy.getMember().getId())
            .nickname(memberStudy.getMember().getName())
            .profileImage(memberStudy.getMember().getProfileImage())
            .build()).toList();

        // DTO로 변환하여 반환
        return new StudyMemberResponseDTO(memberDTOS);
    }

    @Override
    public StudyMemberResDTO.StudyHostDTO getStudyHost(Long studyId) {

        // Authorization
        Long memberId = SecurityUtils.getCurrentUserId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        // 스터디 호스트 찾기
        StudyMember studyHost = studyMemberRepository.findByStudyIdAndIsOwned(studyId, true)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_OWNER_NOT_FOUND));

        // 로그인한 회원이 호스트인지 확인
        if (studyHost.getMember().getId().equals(memberId)) {
            return StudyMemberResDTO.StudyHostDTO.toDTO(true, member);
        } else {
            return StudyMemberResDTO.StudyHostDTO.toDTO(false, studyHost.getMember());
        }
    }

    /**
     * 스터디 신청자의 정보를 조회합니다.
     * @param studyId 스터디 ID
     * @param memberId 회원 ID
     * @return 스터디 신청자 정보를 반환합니다.
     * @throws GeneralException 스터디 신청자가 존재하지 않는 경우
     * @throws GeneralException 조회 하는 회원이 스터디 장이 아닌 경우
     * @throws GeneralException 스터디 장은 스터디에 신청할 수 없음
     */
    @Override
    public StudyApplyMemberDTO findStudyApplication(Long studyId, Long memberId) {

        // 로그인한 회원이 해당 스터디 장인지 확인
        if (!isOwner(SecurityUtils.getCurrentUserId(), studyId))
            throw new GeneralException(ErrorStatus._ONLY_STUDY_OWNER_CAN_ACCESS_APPLICANTS);

        // 스터디 신청자 조회
        StudyMember studyMember = studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPLIED)
            .orElseThrow(() -> new GeneralException(ErrorStatus._STUDY_APPLICANT_NOT_FOUND));

        // 스터디 장은 스터디에 신청할 수 없음
        if (studyMember.getIsOwned())
            throw new GeneralException(ErrorStatus._STUDY_OWNER_CANNOT_APPLY);

        // DTO로 변환하여 반환
        return StudyApplyMemberDTO.builder()
            .memberId(studyMember.getMember().getId())
            .studyId(studyMember.getStudy().getId())
            .introduction(studyMember.getIntroduction())
            .nickname(studyMember.getMember().getName())
            .profileImage(studyMember.getMember().getProfileImage())
            .build();
    }

    /**
     * 해당 스터디 신청 여부를 조회합니다. true: 신청, false: 미신청
     * @param studyId 스터디 ID
     * @return 신청 여부와 스터디 ID를 반환합니다.
     * @throws GeneralException 이미 스터디 멤버인 경우
     */
    @Override
    public StudyApplicantDTO isApplied(Long studyId) {
        // 로그인한 회원 ID 조회
        Long currentUserId = SecurityUtils.getCurrentUserId();

        // 이미 스터디 멤버인 경우
        if (isMember(currentUserId, studyId))
            throw new GeneralException(ErrorStatus._ALREADY_STUDY_MEMBER);

        // DTO로 변환하여 반환
        return StudyApplicantDTO.builder()
            .isApplied(studyMemberRepository.existsByMemberIdAndStudyIdAndStatus(currentUserId, studyId, StudyApplicationStatus.APPLIED))
            .studyId(studyId)
            .build();

    }

    /**
     * 회원이 스터디 장인지 확인합니다.
     * @param memberId 확인 하려는 회원 ID
     * @param studyId 확인 하려는 스터디 ID
     * @return 스터디 장 여부를 반환합니다.
     */
    private boolean isOwner(Long memberId, Long studyId) {
        return studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(memberId, studyId, true).isPresent();
    }

    /**
     * 회원이 스터디 구성원인지 확인합니다.
     * @param memberId 확인 하려는 회원 ID
     * @param studyId 확인 하려는 스터디 ID
     * @return 스터디 참여 여부를 반환합니다.
     */
    private boolean isMember(Long memberId, Long studyId) {
        return studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED).isPresent();
    }
/* ----------------------------- 스터디 갤러리 관련 API ------------------------------------- */

    /**
     * 스터디 게시판에 업로드한 이미지 목록을 불러오는 메서드입니다.
     * @param studyId 타겟 스터디의 아이디를 입력 받습니다.
     * @param pageRequest 페이징에 필요한 페이지 번호와 크기를 입력 받습니다.
     * @return 스터디 아이디와 해당 스터디에 업로드된 이미지 목록을 반환합니다.
     */
    @Override
    public StudyImageResponseDTO.ImageListDTO getAllStudyImages(Long studyId, PageRequest pageRequest) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);
        studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        memberRepository.findById(memberId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._MEMBER_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        //=== Feature ===//
        List<StudyImageResponseDTO.ImageDTO> images = storyRepository.findAllByStudyId(studyId, pageRequest)
                .stream()
                .sorted(Comparator.comparing(Story::getCreatedAt).reversed())
                .flatMap(studyPost -> studyPost.getImages().stream())
                .map(StudyImageResponseDTO.ImageDTO::toDTO)
                .toList();

        return StudyImageResponseDTO.ImageListDTO.toDTO(studyId, images);

    }
}
