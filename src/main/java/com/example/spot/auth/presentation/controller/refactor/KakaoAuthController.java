package com.example.spot.auth.presentation.controller.refactor;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spot.auth.application.refactor.KakaoAuthService;
import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import com.example.spot.member.presentation.dto.MemberResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/spot")
@RequiredArgsConstructor
public class KakaoAuthController {

	private final KakaoAuthService kakaoAuthService;

	/* ----------------------------- 카카오 로그인/회원가입 API ------------------------------------- */
	@Tag(name = "테스트 용 API", description = "테스트 용 API")
	@Operation(summary = "!테스트 용! [회원 가입 및 로그인] 카카오 로그인 및 회원가입 ",
			description = """
            ## [회원 가입 및 로그인] 카카오 로그인의 모든 과정이 구현되어 있습니다. 
            가입 테스트를 위해 구현한 테스트 용 카카오 로그인입니다. 
            서버 파트 및 테스트를 원하는 분들은 본 API로 회원 가입 및 로그인을 진행하시면 됩니다. 
            Swagger에서 요청하는 것이 아닌, 브라우저에서 직접 요청해주세요. 
            ## www.teamspot.site/spot/login/kakao 
            ## localhost:8080/spot/login/kakao  
            
           생성된 회원의 액세스 토큰과 Email이 반환 됩니다. """)
	@GetMapping("/login/kakao")
	public void login() throws IOException {
		kakaoAuthService.redirectURL();
	}

	@Tag(name = "테스트 용 API", description = "테스트 용 API")
	@Operation(summary = "!서버 용! [회원 가입 및 로그인] 카카오 로그인 및 회원가입 리다이렉트용 API ",
			description = """
            ## [회원 가입 및 로그인] 카카오 로그인의 모든 과정이 구현되어 있습니다. 
            가입 테스트를 위해 구현한 테스트 용 리다이렉트 URL입니다. 
            서버 파트 및 테스트를 원하는 분들은 본 API로 회원 가입 및 로그인을 진행하시면 됩니다. 
            Swagger에서 요청하는 것이 아닌, 브라우저에서 직접 요청해주세요. 
            ## www.teamspot.site/spot/login/kakao
            ## localhost:8080/spot/login/kakao  
            
           생성된 회원의 액세스 토큰과 Email이 반환 됩니다. """)
	@GetMapping("/members/sign-in/kakao/redirect")
	public ApiResponse<MemberResponseDTO.SocialLoginSignInDTO> redirectURL(@RequestParam String code) throws IOException {
		MemberResponseDTO.SocialLoginSignInDTO dto = kakaoAuthService.signUpByKAKAOForTest(code);
		return ApiResponse.onSuccess(SuccessStatus._MEMBER_CREATED, dto);
	}

	@Tag(name = "회원 관리 API", description = "회원 관리 API")
	@Operation(summary = "[회원 가입 및 로그인] 카카오 로그인 및 회원가입. ",
			description = """
            ## [회원 가입 및 로그인] 프론트에서 발급 밭은 액세스 토큰을 통해 회원 가입 및 로그인을 진행합니다. 
            연동을 위해 구현된 API입니다. 발급 받은 accessToken을 Param에 첨부하여 API를 호출해주세요.
            생성된 회원의 액세스 토큰과 Email이 반환 됩니다. 
            """)

	@Parameter(name = "accessToken", description = "카카오 액세스 토큰을 입력 해 주세요. ", required = true)
	@GetMapping("/members/sign-in/kakao")
	public ApiResponse<MemberResponseDTO.SocialLoginSignInDTO> signInByKaKao(@RequestParam String accessToken) throws JsonProcessingException {
		MemberResponseDTO.SocialLoginSignInDTO dto = kakaoAuthService.signUpByKAKAO(accessToken);
		return ApiResponse.onSuccess(SuccessStatus._MEMBER_CREATED, dto);
	}
}
