package com.example.spot.member.application.refactor.impl;

import static com.example.spot.member.presentation.dto.MemberResponseDTO.MemberRegionDTO.RegionDTO;
import static com.example.spot.member.presentation.dto.MemberResponseDTO.MemberRegionDTO.builder;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.GeneralException;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.member.application.refactor.MemberPreferenceService;
import com.example.spot.member.domain.Member;
import com.example.spot.member.domain.association.PreferredRegion;
import com.example.spot.member.domain.association.PreferredTheme;
import com.example.spot.member.domain.association.StudyJoinReason;
import com.example.spot.member.domain.enums.Reason;
import com.example.spot.member.infrastructure.MemberRepository;
import com.example.spot.member.infrastructure.PreferredRegionRepository;
import com.example.spot.member.infrastructure.PreferredThemeRepository;
import com.example.spot.member.infrastructure.StudyJoinReasonRepository;
import com.example.spot.member.presentation.dto.MemberRequestDTO;
import com.example.spot.member.presentation.dto.MemberResponseDTO;
import com.example.spot.study.domain.association.Region;
import com.example.spot.study.domain.association.Theme;
import com.example.spot.study.domain.enums.ThemeType;
import com.example.spot.study.domain.repository.RegionRepository;
import com.example.spot.study.domain.repository.ThemeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberPreferenceServiceImpl implements MemberPreferenceService {

    private final MemberRepository memberRepository;

    private final RegionRepository regionRepository;
    private final ThemeRepository themeRepository;

    private final PreferredThemeRepository preferredThemeRepository;
    private final PreferredRegionRepository preferredRegionRepository;
    private final StudyJoinReasonRepository studyJoinReasonRepository;

    /**
     * 회원의 테마 정보를 업데이트합니다.
     *
     * @param memberId   회원 ID
     * @param requestDTO 업데이트할 테마 정보
     * @return 업데이트된 회원 정보와 업데이트 시간
     * @throws MemberHandler    회원이 존재하지 않을 경우
     * @throws GeneralException 테마가 존재하지 않을 경우
     */
    @Override
    public MemberResponseDTO.MemberUpdateDTO updateTheme(Long memberId, MemberRequestDTO.MemberThemeDTO requestDTO) {
        // 회원 조회
        Member member = memberRepository.getById(memberId);

        // 테마 정보 조회
        List<Theme> themes = requestDTO.getThemes().stream()
                .map(themeRepository::getByThemeType)
                .toList();

        // MemberTheme 객체 생성
        List<PreferredTheme> preferredThemes = themes.stream()
                .map(theme -> PreferredTheme.of(member, theme))
                .toList();

        // 기존의 MemberTheme 삭제
        if (preferredThemeRepository.existsByMemberId(member.getId())) {
            preferredThemeRepository.deleteByMemberId(member.getId());
        }

        // 새로운 테마 정보 업데이트
        preferredThemeRepository.saveAll(preferredThemes);

        // 업데이트된 회원 정보 반환
        return MemberResponseDTO.MemberUpdateDTO.builder()
                .memberId(member.getId())
                .updatedAt(member.getUpdatedAt())
                .build();
    }

    /**
     * 회원의 지역 정보를 업데이트합니다.
     *
     * @param memberId   회원 ID
     * @param requestDTO 업데이트할 지역 정보
     * @return 업데이트된 회원 정보와 업데이트 시간
     * @throws MemberHandler    회원이 존재하지 않을 경우
     * @throws GeneralException 지역이 존재하지 않을 경우
     */
    @Override
    public MemberResponseDTO.MemberUpdateDTO updateRegion(Long memberId, MemberRequestDTO.MemberRegionDTO requestDTO) {
        // 회원 조회
        Member member = memberRepository.getById(memberId);

        // 지역 정보 조회
        List<Region> regions = requestDTO.getRegions().stream()
                .map(regionRepository::getByCode)
                .toList();

        // PreferredRegion 객체 생성
        List<PreferredRegion> preferredRegions = regions.stream()
                .map(region -> PreferredRegion.of(member, region))
                .toList();

        // 기존의 MemberTheme과 PreferredRegion 삭제
        if (preferredRegionRepository.existsByMemberId(member.getId())) {
            preferredRegionRepository.deleteByMemberId(member.getId());
        }

        // 새로운 PreferredRegion을 저장
        preferredRegionRepository.saveAll(preferredRegions);

        // 업데이트된 회원 정보 반환
        return MemberResponseDTO.MemberUpdateDTO.builder()
                .memberId(member.getId())
                .updatedAt(member.getUpdatedAt())
                .build();
    }

    /**
     * 회원의 스터디 참여 이유를 변경합니다.
     *
     * @param memberId   변경할 회원 ID
     * @param requestDTO 변경할 이유 정보
     * @return 변경 된 회원 ID와 변경 시간
     * @throws MemberHandler 회원을 찾을 수 없을 경우
     */
    @Override
    public MemberResponseDTO.MemberUpdateDTO updateStudyReason(Long memberId,
                                                               MemberRequestDTO.MemberReasonDTO requestDTO) {
        // 회원 조회
        Member member = memberRepository.getById(memberId);

        // 이유 정보 조회
        List<Reason> reasons = requestDTO.getReasons().stream()
                .map(Reason::fromCode)
                .toList();

        // StudyReason 객체 생성
        List<StudyJoinReason> studyJoinReasons = reasons.stream()
                .map(reason -> StudyJoinReason.of(member, reason.getCode()))
                .toList();

        // 기존의 StudyReason 삭제
        if (studyJoinReasonRepository.existsByMemberId(member.getId())) {
            studyJoinReasonRepository.deleteByMemberId(member.getId());
        }

        // 새로운 StudyReason 저장
        studyJoinReasonRepository.saveAll(studyJoinReasons);

        // 업데이트된 회원 정보 반환
        return MemberResponseDTO.MemberUpdateDTO.builder()
                .memberId(member.getId())
                .updatedAt(member.getUpdatedAt())
                .build();
    }

    /**
     * 회원의 테마 정보를 조회합니다.
     *
     * @param memberId 조회할 회원 ID
     * @return 회원의 테마 정보 및 회원 ID
     * @throws MemberHandler 회원을 찾을 수 없을 경우
     * @throws MemberHandler 회원 테마 정보를 찾을 수 없을 경우
     */
    @Override
    public MemberResponseDTO.MemberThemeDTO getThemes(Long memberId) {
        Member member = memberRepository.getById(memberId);

        List<PreferredTheme> preferredThemes = preferredThemeRepository.findAllByMemberId(member.getId());

        if (preferredThemes.isEmpty()) {
            throw new MemberHandler(ErrorStatus._MEMBER_THEME_NOT_FOUND);
        }

        List<ThemeType> themeTypes = preferredThemes.stream()
                .map(PreferredTheme::getTheme)
                .map(Theme::getThemeType)
                .toList();

        return MemberResponseDTO.MemberThemeDTO.builder()
                .memberId(member.getId())
                .themes(themeTypes)
                .build();
    }


    /**
     * 회원의 지역 정보를 조회합니다.
     *
     * @param memberId 조회할 회원 ID
     * @return 회원의 지역 정보 및 회원 ID
     * @throws MemberHandler 회원을 찾을 수 없을 경우
     * @throws MemberHandler 회원 지역 정보를 찾을 수 없을 경우
     */
    @Override
    public MemberResponseDTO.MemberRegionDTO getRegions(Long memberId) {
        // 회원 조회
        Member member = memberRepository.getById(memberId);

        List<PreferredRegion> preferredRegions = preferredRegionRepository.findAllByMemberId(member.getId());

        // 회원의 지역 정보가 없을 경우
        if (preferredRegions.isEmpty()) {
            throw new MemberHandler(ErrorStatus._MEMBER_REGION_NOT_FOUND);
        }

        // 회원의 지역 정보 조회
        List<RegionDTO> codes = preferredRegions.stream()
                .map(PreferredRegion::getRegion)
                .map(region -> RegionDTO.builder()
                        .province(region.getProvince())
                        .district(region.getDistrict())
                        .neighborhood(region.getNeighborhood())
                        .code(region.getCode())
                        .build())
                .toList();

        // 회원의 지역 정보 반환
        return builder()
                .memberId(member.getId())
                .regions(codes)
                .build();
    }

    /**
     * 회원의 스터디 참여 이유를 조회합니다.
     *
     * @param memberId 조회할 회원 ID
     * @return 회원의 스터디 참여 이유 및 회원 ID
     * @throws MemberHandler 회원을 찾을 수 없을 경우
     * @throws MemberHandler 회원 스터디 참여 이유를 찾을 수 없을 경우
     */
    @Override
    public MemberResponseDTO.MemberStudyReasonDTO getStudyReasons(Long memberId) {
        // 회원 조회
        Member member = memberRepository.getById(memberId);

        List<StudyJoinReason> studyJoinReasons = studyJoinReasonRepository.findAllByMemberId(member.getId());

        // 회원의 스터디 참여 이유가 없을 경우
        if (studyJoinReasons.isEmpty()) {
            throw new MemberHandler(ErrorStatus._MEMBER_STUDY_REASON_NOT_FOUND);
        }

        // 회원의 스터디 참여 이유 ID 조회
        List<Reason> reasons = studyJoinReasons.stream()
                .map(StudyJoinReason::getReason)
                .map(Reason::fromCode)
                .toList();

        // 회원의 스터디 참여 이유 반환
        return MemberResponseDTO.MemberStudyReasonDTO.builder()
                .memberId(member.getId())
                .reasons(reasons)
                .build();
    }
}
