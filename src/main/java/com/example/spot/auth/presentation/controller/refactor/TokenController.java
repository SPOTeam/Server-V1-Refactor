package com.example.spot.auth.presentation.controller.refactor;

import com.example.spot.auth.application.refactor.TokenReissueService;
import com.example.spot.auth.presentation.dto.token.TokenResponseDTO;
import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/spot")
@RequiredArgsConstructor
public class TokenController {

    private final TokenReissueService tokenReissueService;

    /* ----------------------------- JWT 토큰 관리 API ------------------------------------- */

    @Tag(name = "회원 관리 API", description = "회원 관리 API")
    @Operation(summary = "[세션 유지] 액세스 토큰 재발급 API",
            description = """
                    ## [세션 유지] 액세스 토큰을 재발급 하는 API입니다.
                    리프레시 토큰을 통해 액세스 토큰을 재발급 합니다.
                    리프레시 토큰의 만료 기간 이전인 경우에만 재발급이 가능합니다.
                    액세스 토큰을 재발급 하는 경우, 리프레시 토큰도 재발급 됩니다.
                    """)
    @PostMapping("/reissue")
    public ApiResponse<TokenResponseDTO.TokenDTO> reissueToken(HttpServletRequest request,
                                                               @RequestHeader("refreshToken") String refreshToken) {
        return ApiResponse.onSuccess(SuccessStatus._CREATED, tokenReissueService.reissueToken(refreshToken));
    }

}
