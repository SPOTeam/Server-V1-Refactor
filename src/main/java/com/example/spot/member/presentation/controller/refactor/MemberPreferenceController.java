package com.example.spot.member.presentation.controller.refactor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.member.application.refactor.MemberPreferenceService;
import com.example.spot.member.presentation.dto.MemberRequestDTO;
import com.example.spot.member.presentation.dto.MemberResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/spot")
@RequiredArgsConstructor
@Validated
public class MemberPreferenceController {

	private final MemberPreferenceService memberPreferenceService;

	@Tag(name = "회원 관리 API", description = "회원 관리 API")
	@PostMapping("/members/theme")
	@Operation(summary = "[회원 정보 업데이트] 관심 분야 입력 및 수정",
			description = """
            ## [회원 정보 업데이트] 해당하는 회원의 관심 분야를 입력 및 수정 합니다.
            테마를 리스트 형식으로 입력 받습니다.
            대상 회원의 식별 아이디와 수정 시각이 반환 됩니다. 
            """,
			security = @SecurityRequirement(name = "accessToken"))
	public ApiResponse<MemberResponseDTO.MemberUpdateDTO> updateThemes(
			@RequestBody @Valid MemberRequestDTO.MemberThemeDTO requestDTO){
		MemberResponseDTO.MemberUpdateDTO memberUpdateDTO = memberPreferenceService.updateTheme(SecurityUtils.getCurrentUserId(), requestDTO);
		return ApiResponse.onSuccess(SuccessStatus._MEMBER_THEME_UPDATE, memberUpdateDTO);
	}

	@Tag(name = "회원 관리 API", description = "회원 관리 API")
	@PostMapping("/members/region")
	@Operation(summary = "[회원 정보 업데이트] 관심 지역 입력 및 수정",
			description = """
            ## [회원 정보 업데이트] 해당하는 회원의 관심 지역을 입력 및 수정 합니다.
            지역 코드를 리스트 형식으로 입력 받습니다.
            대상 회원의 식별 아이디와 수정 시각이 반환 됩니다. 
            """,
			security = @SecurityRequirement(name = "accessToken"))
	public ApiResponse<MemberResponseDTO.MemberUpdateDTO> updateRegions(
			@RequestBody @Valid MemberRequestDTO.MemberRegionDTO requestDTO){
		MemberResponseDTO.MemberUpdateDTO memberUpdateDTO = memberPreferenceService.updateRegion(SecurityUtils.getCurrentUserId(), requestDTO);
		return ApiResponse.onSuccess(SuccessStatus._MEMBER_REGION_UPDATE, memberUpdateDTO);
	}

	@Tag(name = "회원 관리 API", description = "회원 관리 API")
	@PostMapping("/members/study-reasons")
	@Operation(summary = "[회원 정보 업데이트] 스터디 이유 입력 및 수정",
			description = """
            ## [회원 정보 업데이트] 해당하는 회원의 스터디 이유를 입력 및 수정 합니다.
            업데이트 할 회원의 정보를 입력 받습니다.
            
            꾸준한 학습, 습관이필요해요(1) \n
            상호 피드백이 필요해요(2), \n
            네트워킹을 하고 싶어요(3), \n
            자격증을 취득하고 싶어요(4), \n
            대회에 참가하여 수상하고 싶어요(5),\n 
            다양한 의견을 나누고 싶어요(6); \n
            
            이유에 해당하는 숫자를 리스트 형식으로 입력 받습니다.
            
            대상 회원의 식별 아이디와 수정 시각이 반환 됩니다. 
            """,
			security = @SecurityRequirement(name = "accessToken"))
	public ApiResponse<MemberResponseDTO.MemberUpdateDTO> updateMemberStudyReason(
			@RequestBody @Valid MemberRequestDTO.MemberReasonDTO requestDTO){
		MemberResponseDTO.MemberUpdateDTO memberUpdateDTO = memberPreferenceService.updateStudyReason(SecurityUtils.getCurrentUserId(), requestDTO);
		return ApiResponse.onSuccess(SuccessStatus._MEMBER_INFO_UPDATE, memberUpdateDTO);
	}

	@Tag(name = "회원 조회 API", description = "회원 조회 API")
	@GetMapping("/members/theme")
	@Operation(summary = "[회원 정보 조회] 관심 분야 조회",
			description = """
            ## [회원 정보 조회] 해당하는 회원의 관심 분야를 조회 합니다.
            
            관심 분야를 리스트 형식으로 응답합니다.
            """,
			security = @SecurityRequirement(name = "accessToken"))
	public ApiResponse<MemberResponseDTO.MemberThemeDTO> getThemes(){
		MemberResponseDTO.MemberThemeDTO memberThemeDTO = memberPreferenceService.getThemes(SecurityUtils.getCurrentUserId());
		return ApiResponse.onSuccess(SuccessStatus._MEMBER_THEME_UPDATE, memberThemeDTO);
	}

	@Tag(name = "회원 조회 API", description = "회원 조회 API")
	@GetMapping("/members/region")
	@Operation(summary = "[회원 정보 조회] 관심 지역 조회",
			description = """
            ## [회원 정보 조회] 해당하는 회원의 관심 지역을 조회 합니다.
            
            관심 지역을 리스트 형식으로 응답합니다.
            """,
			security = @SecurityRequirement(name = "accessToken"))
	public ApiResponse<MemberResponseDTO.MemberRegionDTO> getRegions(){
		MemberResponseDTO.MemberRegionDTO memberRegionDTO = memberPreferenceService.getRegions(SecurityUtils.getCurrentUserId());
		return ApiResponse.onSuccess(SuccessStatus._MEMBER_REGION_UPDATE, memberRegionDTO);
	}

	@Tag(name = "회원 조회 API", description = "회원 조회 API")
	@GetMapping("/members/study-reasons")
	@Operation(summary = "[회원 정보 조회] 스터디 이유 조회",
			description = """
            ## [회원 정보 조회] 해당하는 회원의 스터디 이유를 조회 합니다.
            
            스터디 이유를 리스트 형식으로 응답합니다.
            """,
			security = @SecurityRequirement(name = "accessToken"))
	public ApiResponse<MemberResponseDTO.MemberStudyReasonDTO> getStudyReasons(){
		MemberResponseDTO.MemberStudyReasonDTO memberStudyReasonDTO = memberPreferenceService.getStudyReasons(SecurityUtils.getCurrentUserId());
		return ApiResponse.onSuccess(SuccessStatus._MEMBER_FOUND, memberStudyReasonDTO);
	}
}
