package com.example.spot.member.presentation.controller.refactor;

import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.member.application.MemberInfoService;
import com.example.spot.member.presentation.dto.MemberRequestDTO;
import com.example.spot.member.presentation.dto.MemberResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spot")
@RequiredArgsConstructor
@Validated
public class MemberInfoController {

    private final MemberInfoService memberInfoService;

    @Tag(name = "회원 관리 API", description = "회원 관리 API")
    @PostMapping("/members/user-info")
    @Operation(summary = "[회원 정보 업데이트] 개인 정보 입력 및 수정",
            description = """
                    ## [회원 정보 업데이트] 해당하는 회원의 개인 정보를 입력 및 수정 합니다.
                    업데이트 할 회원의 정보를 입력 받습니다.
                    대상 회원의 식별 아이디와 수정 시각이 반환 됩니다. 
                    """,
            security = @SecurityRequirement(name = "accessToken"))
    public ApiResponse<MemberResponseDTO.MemberUpdateDTO> updateMemberInfo(
            @RequestBody @Valid MemberRequestDTO.MemberUpdateDTO requestDTO) {
        MemberResponseDTO.MemberUpdateDTO memberUpdateDTO = memberInfoService.updateProfile(
                SecurityUtils.getCurrentUserId(), requestDTO);
        return ApiResponse.onSuccess(SuccessStatus._MEMBER_INFO_UPDATE, memberUpdateDTO);
    }

}
