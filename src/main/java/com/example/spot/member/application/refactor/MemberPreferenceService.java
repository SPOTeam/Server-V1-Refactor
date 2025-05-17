package com.example.spot.member.application.refactor;

import static com.example.spot.member.presentation.dto.MemberResponseDTO.*;

import com.example.spot.member.presentation.dto.MemberRequestDTO;

public interface MemberPreferenceService {
	MemberUpdateDTO updateTheme(Long memberId, MemberRequestDTO.MemberThemeDTO dto);
	MemberUpdateDTO updateRegion(Long memberId, MemberRequestDTO.MemberRegionDTO dto);
	MemberUpdateDTO updateStudyReason(Long memberId, MemberRequestDTO.MemberReasonDTO dto);

	MemberThemeDTO getThemes(Long memberId);
	MemberRegionDTO getRegions(Long memberId);
	MemberStudyReasonDTO getStudyReasons(Long memberId);
}