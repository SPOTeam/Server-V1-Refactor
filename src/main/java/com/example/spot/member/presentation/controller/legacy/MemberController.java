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

// TODO : 이 클래스는 구글 로그인 API를 위한 레거시 컨트롤러입니다. 추후 리팩토링 예정입니다.

@Deprecated
@RestController
@RequestMapping("/spot")
@RequiredArgsConstructor
@Validated
public class MemberController {

    private final MemberService memberService;

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
