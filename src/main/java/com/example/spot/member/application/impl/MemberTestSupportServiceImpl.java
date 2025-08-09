package com.example.spot.member.application.impl;

import com.example.spot.auth.domain.RefreshToken;
import com.example.spot.auth.infrastructure.jpa.RefreshTokenRepository;
import com.example.spot.auth.presentation.dto.token.TokenResponseDTO;
import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.common.security.utils.JwtTokenProvider;
import com.example.spot.member.application.MemberTestSupportService;
import com.example.spot.member.domain.Member;
import com.example.spot.member.domain.enums.Gender;
import com.example.spot.member.domain.enums.LoginType;
import com.example.spot.member.domain.enums.Status;
import com.example.spot.member.infrastructure.jpa.MemberRepository;
import com.example.spot.member.presentation.dto.MemberRequestDTO;
import com.example.spot.member.presentation.dto.MemberResponseDTO;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberTestSupportServiceImpl implements MemberTestSupportService {
    // JWT
    private final JwtTokenProvider jwtTokenProvider;


    // Repository
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 테스트 회원을 생성합니다.
     *
     * @param memberInfoListDTO 생성할 회원 정보
     * @return 생성된 회원 개인 정보와 토큰
     */
    @Override
    public MemberResponseDTO.MemberTestDTO testMember(MemberRequestDTO.MemberInfoListDTO memberInfoListDTO) {

        // 회원 생성
        Member member = Member.builder()
                .name(memberInfoListDTO.getName())
                .nickname(memberInfoListDTO.getNickname())
                .birth(memberInfoListDTO.getBirth())
                .gender(Gender.UNKNOWN)
                .email(memberInfoListDTO.getEmail())
                .carrier(memberInfoListDTO.getCarrier())
                .phone(memberInfoListDTO.getPhone())
                .password(UUID.randomUUID().toString())
                .profileImage(memberInfoListDTO.getProfileImage())
                .personalInfo(memberInfoListDTO.isPersonalInfo())
                .idInfo(memberInfoListDTO.isIdInfo())
                .loginType(LoginType.NORMAL)
                .status(Status.ON)
                .build();

        // 회원 저장
        memberRepository.save(member);

        // // 테마 정보 저장
        // updateTheme(member.getId(), memberInfoListDTO.getThemes());
        // // 지역 정보 저장
        // updateRegion(member.getId(), memberInfoListDTO.getRegions());

        // 토큰 생성
        TokenResponseDTO.TokenDTO token = jwtTokenProvider.createToken(member.getId());

        // 리프레시 토큰 저장
        saveRefreshToken(member, token);

        // 회원 정보 반환
        return MemberResponseDTO.MemberTestDTO.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .tokens(token)
                .build();
    }

    /**
     * 회원에게 관리자 권한을 부여합니다.
     *
     * @param memberId 관리자로 변경할 회원 ID
     * @return 변경 된 회원 ID와 변경 시간
     * @throws MemberHandler 회원을 찾을 수 없을 경우
     */
    @Override
    public MemberResponseDTO.MemberUpdateDTO toAdmin(Long memberId) {
        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        // 관리자 권한 부여
        member.toAdmin();

        // 회원 정보 저장
        memberRepository.save(member);

        // 변경 된 회원 정보 반환
        return MemberResponseDTO.MemberUpdateDTO.builder()
                .memberId(member.getId())
                .updatedAt(member.getUpdatedAt())
                .build();
    }


    /**
     * 리프레시 토큰을 DB에 저장합니다.
     *
     * @param member 리프레시 토큰을 발급한 회원 정보
     * @param token  발급된 토큰 정보
     */
    private void saveRefreshToken(Member member, TokenResponseDTO.TokenDTO token) {
        // 기존 리프레시 토큰 삭제
        if (refreshTokenRepository.existsByMemberId(member.getId())) {
            refreshTokenRepository.deleteAllByMemberId(member.getId());
        }

        // DB에 저장하기 위한 새로운 리프레시 토큰 객체 생성
        RefreshToken refreshToken = RefreshToken.builder()
                .memberId(member.getId())
                .token(token.getRefreshToken())
                .build();

        // 리프레시 토큰 저장
        refreshTokenRepository.save(refreshToken);
    }
}
