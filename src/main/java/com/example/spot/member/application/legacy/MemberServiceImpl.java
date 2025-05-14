package com.example.spot.member.application.legacy;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.GeneralException;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.member.domain.association.StudyJoinReason;
import com.example.spot.member.domain.enums.Gender;
import com.example.spot.member.domain.enums.LoginType;
import com.example.spot.member.domain.enums.Reason;
import com.example.spot.member.domain.enums.Status;
import com.example.spot.study.domain.enums.ThemeType;
import com.example.spot.member.domain.association.StudyJoinReasonRepository;
import com.example.spot.common.security.utils.JwtTokenProvider;
import com.example.spot.member.domain.Member;
import com.example.spot.member.domain.MemberRepository;
import com.example.spot.member.presentation.dto.MemberRequestDTO;
import com.example.spot.member.presentation.dto.MemberRequestDTO.MemberReasonDTO;
import com.example.spot.auth.domain.CustomUserDetails;
import com.example.spot.auth.domain.RefreshToken;
import com.example.spot.auth.domain.RefreshTokenRepository;
import com.example.spot.auth.infrastructure.kakao.KakaoOAuthClient;
import com.example.spot.member.presentation.dto.MemberResponseDTO;
import com.example.spot.member.presentation.dto.MemberResponseDTO.MemberRegionDTO.RegionDTO;
import com.example.spot.member.presentation.dto.MemberResponseDTO.MemberSignInDTO;
import com.example.spot.member.presentation.dto.MemberResponseDTO.MemberStudyReasonDTO;
import com.example.spot.member.presentation.dto.MemberResponseDTO.MemberTestDTO;
import com.example.spot.member.presentation.dto.MemberResponseDTO.SocialLoginSignInDTO;
import com.example.spot.auth.presentation.dto.kakao.KaKaoOAuthToken.KaKaoOAuthTokenDTO;
import com.example.spot.auth.presentation.dto.kakao.KaKaoUser;
import com.example.spot.auth.presentation.dto.token.TokenResponseDTO.TokenDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.spot.study.domain.aggregate.Region;
import com.example.spot.study.domain.aggregate.Theme;
import com.example.spot.member.domain.association.MemberTheme;
import com.example.spot.member.domain.association.PreferredRegion;
import com.example.spot.member.domain.association.MemberThemeRepository;
import com.example.spot.member.domain.association.PreferredRegionRepository;
import com.example.spot.study.domain.repository.RegionRepository;
import com.example.spot.study.domain.repository.ThemeRepository;
import com.example.spot.member.presentation.dto.MemberRequestDTO.MemberInfoListDTO;
import com.example.spot.member.presentation.dto.MemberRequestDTO.MemberRegionDTO;
import com.example.spot.member.presentation.dto.MemberRequestDTO.MemberThemeDTO;
import com.example.spot.member.presentation.dto.MemberResponseDTO.MemberUpdateDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


// TODO 추후 삭제 예정 -> 구글 로그인 관련 로직이 남아있음

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
@Deprecated
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원의 정보를 조회합니다.
     * @param username 회원 식별자(ID)
     * @return 회원 정보
     * @throws UsernameNotFoundException 회원을 찾을 수 없을 경우
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 회원 ID Long 타입으로 변환
        Long memberId = parseUsernameToMemberId(username);

        // 회원 조회
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 권한 설정 -> ROLE_USER 또는 ROLE_ADMIN
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + (member.getIsAdmin() ? "ADMIN" : "USER"))
        );

        // CustomUserDetails 객체 생성
        return CustomUserDetails.builder()
            .email(member.getEmail())
            .memberId(member.getId())
            .password(member.getPassword())
            .enabled(true)
            .authorities(authorities)
            .build();
    }

    /**
     * 문자열로 입력된 회원 ID를 Long 타입으로 파싱합니다.
     * @param username 회원 ID 문자열
     * @return 회원 ID
     * @throws UsernameNotFoundException 회원 ID 형식이 잘못된 경우
     */
    private Long parseUsernameToMemberId(String username) {
        try {
            return Long.parseLong(username);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid user ID format");
        }
    }


    @Override
    @Transactional
    public void save(Member member) {
        memberRepository.save(member);
    }

}
