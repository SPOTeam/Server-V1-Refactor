package com.example.spot.member.application.refactor.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.GeneralException;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.member.application.refactor.MemberPreferenceService;
import com.example.spot.member.domain.Member;
import com.example.spot.member.domain.MemberRepository;
import com.example.spot.member.domain.association.MemberTheme;
import com.example.spot.member.domain.association.MemberThemeRepository;
import com.example.spot.member.domain.association.PreferredRegion;
import com.example.spot.member.domain.association.PreferredRegionRepository;
import com.example.spot.member.domain.association.StudyJoinReason;
import com.example.spot.member.domain.association.StudyJoinReasonRepository;
import com.example.spot.member.domain.enums.Reason;
import com.example.spot.member.presentation.dto.MemberRequestDTO;
import com.example.spot.member.presentation.dto.MemberResponseDTO;
import com.example.spot.study.domain.association.Region;
import com.example.spot.study.domain.association.Theme;
import com.example.spot.study.domain.enums.ThemeType;
import com.example.spot.study.domain.repository.RegionRepository;
import com.example.spot.study.domain.repository.ThemeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberPreferenceServiceImpl implements MemberPreferenceService {

	private final MemberRepository memberRepository;

	private final RegionRepository regionRepository;
	private final ThemeRepository themeRepository;

	private final MemberThemeRepository memberThemeRepository;
	private final PreferredRegionRepository preferredRegionRepository;
	private final StudyJoinReasonRepository studyJoinReasonRepository;

	/**
	 * 회원의 테마 정보를 업데이트합니다.
	 * @param memberId 회원 ID
	 * @param requestDTO 업데이트할 테마 정보
	 * @return 업데이트된 회원 정보와 업데이트 시간
	 * @throws MemberHandler 회원이 존재하지 않을 경우
	 * @throws GeneralException 테마가 존재하지 않을 경우
	 */
	@Override
	public MemberResponseDTO.MemberUpdateDTO updateTheme(Long memberId, MemberRequestDTO.MemberThemeDTO requestDTO) {
		// 회원 조회
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

		// 테마 정보 조회
		List<Theme> themes = requestDTO.getThemes().stream()
				.map(themeType -> themeRepository.findByThemeType(themeType).orElseThrow(() -> new GeneralException(ErrorStatus._THEME_NOT_FOUND)))
				.toList();

		// MemberTheme 객체 생성
		List<MemberTheme> memberThemes = themes.stream()
				.map(theme -> MemberTheme.builder().member(member).theme(theme).build())
				.toList();

		// 기존의 MemberTheme 삭제
		if (memberThemeRepository.existsByMemberId(member.getId()))
			memberThemeRepository.deleteByMemberId(member.getId());

		// 새로운 MemberTheme과 PreferredRegion을 저장
		memberThemeRepository.saveAll(memberThemes);

		// 회원 정보 업데이트
		member.updateThemes(memberThemes);

		// 회원 정보 저장
		memberRepository.save(member);

		// 업데이트된 회원 정보 반환
		return MemberResponseDTO.MemberUpdateDTO.builder()
				.memberId(member.getId())
				.updatedAt(member.getUpdatedAt())
				.build();
	}

	/**
	 * 회원의 지역 정보를 업데이트합니다.
	 * @param memberId 회원 ID
	 * @param requestDTO 업데이트할 지역 정보
	 * @return 업데이트된 회원 정보와 업데이트 시간
	 * @throws MemberHandler 회원이 존재하지 않을 경우
	 * @throws GeneralException 지역이 존재하지 않을 경우
	 *
	 */
	@Override
	public MemberResponseDTO.MemberUpdateDTO updateRegion(Long memberId, MemberRequestDTO.MemberRegionDTO requestDTO) {
		// 회원 조회
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

		// 지역 정보 조회
		List<Region> regions = requestDTO.getRegions().stream()
				.map(regionCode -> regionRepository.findByCode(regionCode).orElseThrow(() -> new GeneralException(ErrorStatus._REGION_NOT_FOUND)))
				.toList();

		// PreferredRegion 객체 생성
		List<PreferredRegion> preferredRegions = regions.stream()
				.map(region -> PreferredRegion.builder().member(member).region(region).build())
				.toList();

		// 기존의 MemberTheme과 PreferredRegion 삭제
		if (preferredRegionRepository.existsByMemberId(member.getId()))
			preferredRegionRepository.deleteByMemberId(member.getId());

		// 새로운 PreferredRegion을 저장
		preferredRegionRepository.saveAll(preferredRegions);

		// 회원 정보 업데이트
		member.updateRegions(preferredRegions);

		// 회원 정보 저장
		memberRepository.save(member);

		// 업데이트된 회원 정보 반환
		return MemberResponseDTO.MemberUpdateDTO.builder()
				.memberId(member.getId())
				.updatedAt(member.getUpdatedAt())
				.build();
	}

	/**
	 * 회원의 스터디 참여 이유를 변경합니다.
	 * @param memberId 변경할 회원 ID
	 * @param requestDTO 변경할 이유 정보
	 * @return 변경 된 회원 ID와 변경 시간
	 * @throws MemberHandler 회원을 찾을 수 없을 경우
	 */
	@Override
	public MemberResponseDTO.MemberUpdateDTO updateStudyReason(Long memberId, MemberRequestDTO.MemberReasonDTO requestDTO) {
		// 회원 조회
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

		// 이유 정보 조회
		List<Reason> reasons = requestDTO.getReasons().stream()
				.map(Reason::fromCode)
				.toList();

		// StudyReason 객체 생성
		List<StudyJoinReason> studyJoinReasons = reasons.stream()
				.map(reason -> StudyJoinReason.builder().member(member).reason(reason.getCode()).build())
				.toList();

		// 기존의 StudyReason 삭제
		if (studyJoinReasonRepository.existsByMemberId(member.getId()))
			studyJoinReasonRepository.deleteByMemberId(member.getId());

		// 새로운 StudyReason 저장
		studyJoinReasonRepository.saveAll(studyJoinReasons);

		// 회원 정보 업데이트
		member.updateReasons(studyJoinReasons);

		// 회원 정보 저장
		memberRepository.save(member);

		// 업데이트된 회원 정보 반환
		return MemberResponseDTO.MemberUpdateDTO.builder()
				.memberId(member.getId())
				.updatedAt(member.getUpdatedAt())
				.build();
	}

	/**
	 * 회원의 테마 정보를 조회합니다.
	 * @param memberId 조회할 회원 ID
	 * @return 회원의 테마 정보 및 회원 ID
	 * @throws MemberHandler 회원을 찾을 수 없을 경우
	 * @throws MemberHandler 회원 테마 정보를 찾을 수 없을 경우
	 */
	@Override
	public MemberResponseDTO.MemberThemeDTO getThemes(Long memberId) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

		if (member.getMemberThemeList().isEmpty())
			throw new MemberHandler(ErrorStatus._MEMBER_THEME_NOT_FOUND);

		List<Theme> themes = member.getMemberThemeList().stream()
				.map(MemberTheme::getTheme)
				.toList();

		List<ThemeType> themeTypes = themes.stream()
				.map(Theme::getThemeType)
				.toList();

		return MemberResponseDTO.MemberThemeDTO.builder()
				.memberId(member.getId())
				.themes(themeTypes)
				.build();
	}


	/**
	 * 회원의 지역 정보를 조회합니다.
	 * @param memberId 조회할 회원 ID
	 * @return 회원의 지역 정보 및 회원 ID
	 * @throws MemberHandler 회원을 찾을 수 없을 경우
	 * @throws MemberHandler 회원 지역 정보를 찾을 수 없을 경우
	 */
	@Override
	public MemberResponseDTO.MemberRegionDTO getRegions(Long memberId) {
		// 회원 조회
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

		// 회원의 지역 정보가 없을 경우
		if (member.getRegions().isEmpty())
			throw new MemberHandler(ErrorStatus._MEMBER_REGION_NOT_FOUND);

		// 회원의 지역 정보 조회
		List<Region> regions = member.getPreferredRegionList().stream()
				.map(PreferredRegion::getRegion)
				.toList();

		// 지역 정보 DTO로 변환
		List<MemberResponseDTO.MemberRegionDTO.RegionDTO> codes = regions.stream()
				.map(region -> MemberResponseDTO.MemberRegionDTO.RegionDTO.builder()
						.province(region.getProvince())
						.district(region.getDistrict())
						.neighborhood(region.getNeighborhood())
						.code(region.getCode())
						.build())
				.toList();

		// 회원의 지역 정보 반환
		return MemberResponseDTO.MemberRegionDTO.builder()
				.memberId(member.getId())
				.regions(codes)
				.build();
	}

	/**
	 * 회원의 스터디 참여 이유를 조회합니다.
	 * @param memberId 조회할 회원 ID
	 * @return 회원의 스터디 참여 이유 및 회원 ID
	 * @throws MemberHandler 회원을 찾을 수 없을 경우
	 * @throws MemberHandler 회원 스터디 참여 이유를 찾을 수 없을 경우
	 */
	@Override
	public MemberResponseDTO.MemberStudyReasonDTO getStudyReasons(Long memberId) {
		// 회원 조회
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

		// 회원의 스터디 참여 이유가 없을 경우
		if (member.getStudyJoinReasonList().isEmpty())
			throw new MemberHandler(ErrorStatus._MEMBER_STUDY_REASON_NOT_FOUND);

		// 회원의 스터디 참여 이유 ID 조회
		List<Long> reasonNums = member.getStudyJoinReasonList().stream()
				.map(StudyJoinReason::getReason)
				.toList();

		// 이유 ID를 이유 객체로 변환
		List<Reason> reasons = reasonNums.stream()
				.map(Reason::fromCode)
				.toList();

		// 회원의 스터디 참여 이유 반환
		return MemberResponseDTO.MemberStudyReasonDTO.builder()
				.memberId(member.getId())
				.reasons(reasons)
				.build();
	}
}
