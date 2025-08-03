package com.example.spot.member.presentation.controller.refactor;

import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.member.application.MemberTestSupportService;
import com.example.spot.member.presentation.dto.MemberRequestDTO;
import com.example.spot.member.presentation.dto.MemberResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
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
public class MemberTestSupportController {

    private final MemberTestSupportService memberTestSupportService;

    @Tag(name = "테스트 용 API", description = "테스트 용 API")
    @Operation(summary = "!테스트 용! [회원 생성] 테스트 용 회원 생성 API",
            description = """
                     ## [테스트 용 회원 생성] 임의의 정보를 가진 회원 객체가 생성 됩니다. 
                     다른 API를 테스트 하기 위해 회원이 필요한 경우 사용해주세요.
                     회원의 관심 분야 및 지역을 입력 받습니다.   
                    생성된 회원의 ID와 Email이 반환 됩니다. """)
    @PostMapping("/members/test")
    public ApiResponse<MemberResponseDTO.MemberTestDTO> testMember(
            @RequestBody @Valid MemberRequestDTO.MemberInfoListDTO memberInfoListDTO) {
        MemberResponseDTO.MemberTestDTO dto = memberTestSupportService.testMember(memberInfoListDTO);
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
    public ApiResponse<MemberResponseDTO.MemberUpdateDTO> toAdmin() {
        MemberResponseDTO.MemberUpdateDTO dto = memberTestSupportService.toAdmin(SecurityUtils.getCurrentUserId());
        return ApiResponse.onSuccess(SuccessStatus._MEMBER_CREATED, dto);
    }
}
