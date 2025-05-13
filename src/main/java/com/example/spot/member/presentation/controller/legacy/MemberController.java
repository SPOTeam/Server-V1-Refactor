package com.example.spot.member.presentation.controller.legacy;

import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;

import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.member.application.legacy.MemberService;
import com.example.spot.member.presentation.dto.MemberResponseDTO;
import com.example.spot.member.presentation.dto.MemberResponseDTO.MemberRegionDTO;
import com.example.spot.member.presentation.dto.MemberResponseDTO.MemberStudyReasonDTO;
import com.example.spot.member.presentation.dto.MemberResponseDTO.MemberTestDTO;
import com.example.spot.member.presentation.dto.MemberRequestDTO;
import com.example.spot.member.presentation.dto.MemberResponseDTO.MemberUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.spot.auth.presentation.dto.google.GoogleExampleResponse.EXAMPLE_RESPONSE;

@RestController
@RequestMapping("/spot")
@RequiredArgsConstructor
@Validated
public class MemberController {

    private final MemberService memberService;
    @Tag(name = "테스트 용 API", description = "테스트 용 API")
    @Operation(summary = "!테스트 용! [회원 생성] 테스트 용 회원 생성 API",
        description = """
            ## [테스트 용 회원 생성] 임의의 정보를 가진 회원 객체가 생성 됩니다. 
            다른 API를 테스트 하기 위해 회원이 필요한 경우 사용해주세요.
            회원의 관심 분야 및 지역을 입력 받습니다.   
           생성된 회원의 ID와 Email이 반환 됩니다. """)
    @PostMapping("/members/test")
    public ApiResponse<MemberResponseDTO.MemberTestDTO> testMember(
        @RequestBody @Valid MemberRequestDTO.MemberInfoListDTO memberInfoListDTO){
        MemberTestDTO dto = memberService.testMember(memberInfoListDTO);
        return ApiResponse.onSuccess(SuccessStatus._MEMBER_CREATED, dto);
    }

    @Tag(name = "테스트 용 API", description = "테스트 용 API")
    @Operation(summary = "!테스트 용! [회원 권한 부여] 관리자 권한 부여 API",
        description = """
            ## [회원 권한 부여] 해당하는 회원에게 관리자 권한을 부여합니다.
            테스트를 위해 구현한 테스트 용 API입니다.
            회원의 ID를 입력 받아 관리자 권한을 부여합니다.
            성공 여부와 회원 ID가 반환 됩니다.
             """)
    @PostMapping("/members/test/admin")
    public ApiResponse<MemberResponseDTO.MemberUpdateDTO> toAdmin(){
        MemberUpdateDTO dto = memberService.toAdmin(SecurityUtils.getCurrentUserId());
        return ApiResponse.onSuccess(SuccessStatus._MEMBER_CREATED, dto);
    }



    @Tag(name = "회원 관리 API", description = "회원 관리 API")
    @PostMapping("/members/user-info")
    @Operation(summary = "[회원 정보 업데이트] 개인 정보 입력 및 수정",
        description = """
            ## [회원 정보 업데이트] 해당하는 회원의 개인 정보를 입력 및 수정 합니다.
            업데이트 할 회원의 정보를 입력 받습니다.
            대상 회원의 식별 아이디와 수정 시각이 반환 됩니다. 
            """,
        security = @SecurityRequirement(name = "accessToken"))
    public ApiResponse<MemberUpdateDTO> updateMemberInfo(
        @RequestBody @Valid MemberRequestDTO.MemberUpdateDTO requestDTO){
        MemberUpdateDTO memberUpdateDTO = memberService.updateProfile(SecurityUtils.getCurrentUserId(), requestDTO);
        return ApiResponse.onSuccess(SuccessStatus._MEMBER_INFO_UPDATE, memberUpdateDTO);
    }




    @Tag(name = "구글 로그인 API", description = "구글 OAuth2 로그인 API")
    @Operation(summary = "[구글 로그인] 구글 로그인/회원가입 API",
            description = """
               구글 로그인 인증 페이지로 이동합니다.
               사용자가 로그인 후, 설정된 리디렉션 URL로 돌아옵니다.
               브라우저에서 직접 요청해 주세요.
               ## http://localhost:8080/oauth2/authorization/google
               ## www.teamspot.site/oauth2/authorization/google
               """)
    @GetMapping("/oauth/authorize")
    public void redirectToGoogleLogin(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", "/oauth/authorize");
    }

    @Tag(name = "구글 로그인 API", description = "구글 OAuth2 로그인 API")
    @Operation(summary = "[구글 로그인] 구글 로그인/회원가입 리다이렉트용 API",
            description = """
               구글 로그인 인증 완료 후 호출되는 콜백 URL입니다.
               클라이언트가 직접 호출하지 않습니다.
               로그인 성공 시 회원의 이메일과 토큰 정보를 반환합니다.
               """)
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "google OAuth 로그인에 성공하면 SPOT 서버에 접근할 수 있는 SPOT JWT Token을 반환합니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponses.class),
                            examples = @ExampleObject(
                                    value = EXAMPLE_RESPONSE
                            )))})
    @GetMapping("/members/sign-in/google/redirect")
    public void handleGoogleCallback() {
    }

}
