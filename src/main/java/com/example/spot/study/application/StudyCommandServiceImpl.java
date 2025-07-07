package com.example.spot.study.application;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.common.api.exception.handler.StudyHandler;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.member.domain.Member;
import com.example.spot.member.domain.association.PreferredStudy;
import com.example.spot.member.domain.enums.Status;
import com.example.spot.member.infrastructure.MemberRepository;
import com.example.spot.member.infrastructure.PreferredStudyRepository;
import com.example.spot.study.domain.Study;
import com.example.spot.study.domain.StudyRepository;
import com.example.spot.study.domain.association.Region;
import com.example.spot.study.domain.association.StudyMember;
import com.example.spot.study.domain.association.StudyRegion;
import com.example.spot.study.domain.association.StudyTheme;
import com.example.spot.study.domain.association.Theme;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.study.domain.enums.StudyLikeStatus;
import com.example.spot.study.domain.enums.StudyState;
import com.example.spot.study.domain.repository.RegionRepository;
import com.example.spot.study.domain.repository.StudyMemberRepository;
import com.example.spot.study.domain.repository.StudyRegionRepository;
import com.example.spot.study.domain.repository.StudyThemeRepository;
import com.example.spot.study.domain.repository.ThemeRepository;
import com.example.spot.study.presentation.dto.request.StudyMemberRequestDTO;
import com.example.spot.study.presentation.dto.request.StudyMemberRequestDTO.RegisterDTO;
import com.example.spot.study.presentation.dto.response.StudyMemberResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyResponseDTO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class StudyCommandServiceImpl implements StudyCommandService {


    @Value("${study.keyword}")
    private String KEYWORD;

    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;
    private final RegionRepository regionRepository;
    private final ThemeRepository themeRepository;

    private final StudyMemberRepository studyMemberRepository;
    private final StudyRegionRepository studyRegionRepository;
    private final StudyThemeRepository studyThemeRepository;
    private final PreferredStudyRepository preferredStudyRepository;

    private final RedisTemplate<String, String> redisTemplate;

    /* ----------------------------- 스터디 생성/참여 관련 API ------------------------------------- */

    // [스터디 생성/참여] 참여 신청하기
    @Transactional
    public StudyMemberResponseDTO.JoinDTO applyToStudy(Long studyId,
                                                       StudyMemberRequestDTO.@Valid JoinDTO studyJoinRequestDTO) {

        // Authorization
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        // 모집중이지 않은 스터디에 신청할 수 없음
        if (study.getStudyState() != StudyState.RECRUITING) {
            throw new StudyHandler(ErrorStatus._STUDY_NOT_RECRUITING);
        }

        if (study.getMaxPeople() <= studyMemberRepository.countByStatusAndStudyId(StudyApplicationStatus.APPROVED,
                studyId)) {
            throw new StudyHandler(ErrorStatus._STUDY_IS_FULL);
        }

        // 이미 신청한 스터디에 다시 신청할 수 없음
        List<StudyMember> studyMemberList = studyMemberRepository.findByMemberIdAndStatusNot(memberId,
                        StudyApplicationStatus.REJECTED).stream()
                .filter(memberStudy -> study.equals(memberStudy.getStudy()))
                .toList();

        // memberStudy에 내가 소유한 스터디가 있으면 에러 발생
        if (studyMemberList.stream().anyMatch(StudyMember::getIsOwned)) {
            throw new StudyHandler(ErrorStatus._STUDY_OWNER_CANNOT_APPLY);
        }

        if (!studyMemberList.isEmpty()) {
            throw new StudyHandler(ErrorStatus._STUDY_ALREADY_APPLIED);
        }

        StudyMember studyMember = StudyMember.builder()
                .isOwned(false)
                .introduction(studyJoinRequestDTO.getIntroduction())
                .member(member)
                .study(study)
                .status(StudyApplicationStatus.APPLIED)
                .build();

        member.addMemberStudy(studyMember);
        study.addMemberStudy(studyMember);
        studyMemberRepository.save(studyMember);

        return StudyMemberResponseDTO.JoinDTO.toDTO(member, study);
    }

    // [스터디 생성/참여] 스터디 생성하기
    @Transactional
    public StudyResponseDTO.RegisterDTO registerStudy(StudyMemberRequestDTO.RegisterDTO studyRegisterRequestDTO) {

        // Authorization
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        Study study = Study.builder()
                .gender(studyRegisterRequestDTO.getGender())
                .minAge(studyRegisterRequestDTO.getMinAge())
                .maxAge(studyRegisterRequestDTO.getMaxAge())
                .hasFee(studyRegisterRequestDTO.isHasFee())
                .fee(studyRegisterRequestDTO.getFee())
                .profileImage(studyRegisterRequestDTO.getProfileImage())
                .studyState(StudyState.RECRUITING)
                .isOnline(studyRegisterRequestDTO.getIsOnline())
                .heartCount(0)
                .goal(studyRegisterRequestDTO.getGoal())
                .introduction(studyRegisterRequestDTO.getIntroduction())
                .title(studyRegisterRequestDTO.getTitle())
                .status(Status.ON)
                .hitNum(0L)
                .maxPeople(studyRegisterRequestDTO.getMaxPeople())
                .build();

        study = studyRepository.save(study);

        createMemberStudy(member, study);
        createRegionStudy(study, studyRegisterRequestDTO);
        createStudyTheme(study, studyRegisterRequestDTO);

        studyRepository.save(study);

        return StudyResponseDTO.RegisterDTO.toDTO(study);
    }


    /**
     * 스터디 정보를 수정합니다.
     *
     * @param studyId      수정할 스터디 ID
     * @param studyInfoDTO 수정할 스터디 정보
     * @return 수정된 스터디 정보를 반환합니다.
     */

    @Override
    public StudyResponseDTO.RegisterDTO updateStudyInfo(Long studyId, RegisterDTO studyInfoDTO) {

        Long currentUserId = SecurityUtils.getCurrentUserId();

        StudyMember studyMember = studyMemberRepository.findByMemberIdAndStudyId(currentUserId, studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        if (!studyMember.getIsOwned()) {
            throw new StudyHandler(ErrorStatus._STUDY_NOT_OWNER);
        }

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        study.updateStudyInfo(studyInfoDTO.getTitle(), studyInfoDTO.getIntroduction(), studyInfoDTO.getGoal(),
                studyInfoDTO.getIsOnline(),
                studyInfoDTO.isHasFee(), studyInfoDTO.getFee(), studyInfoDTO.getMinAge(), studyInfoDTO.getMaxAge(),
                studyInfoDTO.getGender(), studyInfoDTO.getMaxPeople(), studyInfoDTO.getProfileImage());

        studyThemeRepository.deleteByStudyId(studyId);
        study.getStudyThemes().clear();

        studyRegionRepository.deleteByStudyId(studyId);
        study.getRegionStudies().clear();

        createRegionStudy(study, studyInfoDTO);
        createStudyTheme(study, studyInfoDTO);

        studyRepository.save(study);

        return StudyResponseDTO.RegisterDTO.toDTO(study);
    }

    /**
     * 특정 스터디에 좋아요를 누르거나 취소합니다. 이미 좋아요가 눌려있다면 취소하고, 아니라면 좋아요를 누릅니다.
     *
     * @param memberId 회원 ID
     * @param studyId  스터디 ID
     * @return 스터디 제목과 좋아요 상태를 반환합니다.
     * @throws StudyHandler  스터디가 존재하지 않는 경우
     * @throws MemberHandler 회원이 존재하지 않는 경우
     */
    @Override
    public StudyResponseDTO.LikeDTO likeStudy(Long memberId, Long studyId) {

        // 회원과 스터디 조회
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        // 현재 좋아요 상태 확인 -> 만약 없다면, 객체 하나 생성
        PreferredStudy preferredStudy = preferredStudyRepository
                .findByMemberIdAndStudyId(memberId, studyId)
                .orElse(PreferredStudy.builder()
                        .member(member)
                        .study(study)
                        .studyLikeStatus(StudyLikeStatus.DISLIKE)
                        .build());

        // 상태에 따라 변경
        if (preferredStudy.getStudyLikeStatus() == StudyLikeStatus.LIKE) {
            preferredStudy.changeStatus(StudyLikeStatus.DISLIKE);
            study.deletePreferredStudy(preferredStudy);
        } else {
            preferredStudy.changeStatus(StudyLikeStatus.LIKE);
            study.addPreferredStudy(preferredStudy);
        }
        // 저장 및 응답 객체 생성
        preferredStudyRepository.save(preferredStudy);
        return new StudyResponseDTO.LikeDTO(preferredStudy);
    }


    private void createMemberStudy(Member member, Study study) {

        StudyMember studyMember = StudyMember.builder()
                .isOwned(true)
                .introduction(study.getIntroduction())
                .member(member)
                .study(study)
                .status(StudyApplicationStatus.APPROVED)
                .build();

        member.addMemberStudy(studyMember);
        study.addMemberStudy(studyMember);
        studyMemberRepository.save(studyMember);

        study.addMemberStudy(studyMember);

    }

    private void createRegionStudy(Study study, StudyMemberRequestDTO.RegisterDTO studyRegisterRequestDTO) {

        studyRegisterRequestDTO.getRegions()
                .forEach(stringRegion -> {

                    Region region = regionRepository
                            .findByCode(stringRegion)
                            .orElseThrow(() -> new StudyHandler(ErrorStatus._REGION_NOT_FOUND));

                    StudyRegion studyRegion = StudyRegion.builder()
                            .region(region)
                            .study(study)
                            .build();

                    region.addRegionStudy(studyRegion);
                    study.addRegionStudy(studyRegion);
                    studyRegionRepository.save(studyRegion);

                    study.addRegionStudy(studyRegion);
                });
    }

    private void createStudyTheme(Study study, StudyMemberRequestDTO.RegisterDTO studyRegisterRequestDTO) {

        studyRegisterRequestDTO.getThemes()
                .forEach(stringTheme -> {

                    Theme theme = themeRepository.findByThemeType(stringTheme)
                            .orElseThrow(() -> new StudyHandler(ErrorStatus._THEME_NOT_FOUND));

                    StudyTheme studyTheme = StudyTheme.builder()
                            .theme(theme)
                            .study(study)
                            .build();

                    study.addStudyTheme(studyTheme);
                    theme.addStudyTheme(studyTheme);
                    studyThemeRepository.save(studyTheme);

                    study.addStudyTheme(studyTheme);
                });
    }

    /* ---------------------------------- 인기 검색어 --------------------------------------------- */

    /**
     * 검색어를 인기 검색어(Redis)에 추가합니다. 이미 존재하는 검색어라면 score를 1 증가시킵니다.
     *
     * @param keyword 검색어
     */
    @Override
    public void addHotKeyword(String keyword) {
        redisTemplate.opsForZSet().incrementScore(KEYWORD, keyword, 1);
    }
}
