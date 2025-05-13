package com.example.spot.auth.application.impl;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.spot.auth.application.KakaoAuthService;
import com.example.spot.auth.domain.RefreshToken;
import com.example.spot.auth.domain.RefreshTokenRepository;
import com.example.spot.auth.infrastructure.kakao.KakaoOAuthClient;
import com.example.spot.auth.presentation.dto.kakao.KaKaoOAuthToken;
import com.example.spot.auth.presentation.dto.kakao.KaKaoUser;
import com.example.spot.auth.presentation.dto.token.TokenResponseDTO;
import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.GeneralException;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.common.security.utils.JwtTokenProvider;
import com.example.spot.member.domain.Member;
import com.example.spot.member.domain.MemberRepository;
import com.example.spot.member.domain.association.MemberThemeRepository;
import com.example.spot.member.domain.association.PreferredRegionRepository;
import com.example.spot.member.domain.association.StudyJoinReasonRepository;
import com.example.spot.member.domain.enums.LoginType;
import com.example.spot.member.presentation.dto.MemberResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class KakaoAuthServiceImpl implements KakaoAuthService {

	@PersistenceContext
	private EntityManager entityManager;

	private final KakaoOAuthClient client;

	private final JwtTokenProvider jwtTokenProvider;

	private final HttpServletResponse response;

	private final MemberRepository memberRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final MemberThemeRepository memberThemeRepository;
	private final PreferredRegionRepository preferredRegionRepository;
	private final StudyJoinReasonRepository studyJoinReasonRepository;

	/**
	 * 카카오 로그인을 통해 회원 가입 또는 로그인을 수행합니다.
	 * @param accessToken 카카오 OAuth 액세스 토큰
	 * @return SPOT 서버에서 발급한 JWT 토큰 및 회원 정보
	 * @throws JsonProcessingException 카카오 사용자 정보 파싱 중 발생하는 예외
	 */
	@Override
	public MemberResponseDTO.SocialLoginSignInDTO signUpByKAKAO(String accessToken) throws JsonProcessingException {
		// 액세스 토큰을 사용하여 사용자 정보 요청
		ResponseEntity<String> userInfoResponse = client.requestUserInfo(accessToken);

		// 응답에서 사용자 정보를 파싱
		KaKaoUser kaKaoUser = client.getUserInfo(userInfoResponse);

		if (memberRepository.existsByEmailAndLoginTypeNot(kaKaoUser.toMember().getEmail(), LoginType.KAKAO)){
			log.info(kaKaoUser.toMember().getEmail());
			throw new GeneralException(ErrorStatus._MEMBER_EMAIL_EXIST);
		}

		Boolean isSpotMember = false;
		// 사용자가 이미 존재하는지 확인
		if (memberRepository.existsByEmail(kaKaoUser.toMember().getEmail())) {
			// 존재하는 경우, 사용자 정보를 가져옴
			Member member = memberRepository.findByEmail(kaKaoUser.toMember().getEmail())
					.orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

			updateMemberProfileImage(member, kaKaoUser);

			if (isMemberExistsByCheckList(member)) {
				isSpotMember = true;
			}

			// JWT 토큰 생성
			TokenResponseDTO.TokenDTO token = jwtTokenProvider.createToken(member.getId());

			saveRefreshToken(member, token);

			// 로그인 DTO 반환
			MemberResponseDTO.MemberSignInDTO dto = MemberResponseDTO.MemberSignInDTO.builder()
					.tokens(token)
					.memberId(member.getId())
					.loginType(member.getLoginType())
					.email(member.getEmail())
					.build();
			return MemberResponseDTO.SocialLoginSignInDTO.toDTO(isSpotMember, dto);
		}


		// 존재하지 않는 경우, 새로운 회원 정보 저장
		Member member = memberRepository.save(kaKaoUser.toMember());

		// JWT 토큰 생성
		TokenResponseDTO.TokenDTO token = jwtTokenProvider.createToken(member.getId());

		saveRefreshToken(member, token);

		// 회원 가입 DTO 반환
		MemberResponseDTO.MemberSignInDTO dto = MemberResponseDTO.MemberSignInDTO.builder()
				.tokens(token)
				.memberId(member.getId())
				.loginType(member.getLoginType())
				.email(member.getEmail())
				.build();
		return MemberResponseDTO.SocialLoginSignInDTO.toDTO(isSpotMember, dto);
	}

	private void updateMemberProfileImage(Member member, KaKaoUser kaKaoUser) {
		if (!member.getProfileImage().equals(kaKaoUser.getProperties().getProfile_image())) {
			member.updateProfileImage(kaKaoUser.getProperties().getProfile_image());
		}
	}

	/**
	 * 카카오 로그인을 테스트용으로 수행합니다. 카카오 auth accessToken 발급을 포함한 모든 내부 로직이 구현 되어 있습니다.
	 * @param code 카카오 로그인 요청 시 발급받은 코드
	 * @return SPOT 서버에서 발급한 JWT 토큰 및 회원 정보
	 * @throws JsonProcessingException 카카오 사용자 정보 파싱 중 발생하는 예외
	 * @throws MemberHandler 이메일로 가입된 내역이 존재하지만, 실제로는 회원이 존재하지 않을 경우
	 */
	@Override
	public MemberResponseDTO.SocialLoginSignInDTO signUpByKAKAOForTest(String code) throws JsonProcessingException {
		// 카카오 OAuth 서비스에서 액세스 토큰 요청
		ResponseEntity<String> accessTokenResponse = client.requestAccessToken(code);

		// 응답에서 액세스 토큰을 파싱
		KaKaoOAuthToken.KaKaoOAuthTokenDTO oAuthToken = client.getAccessToken(accessTokenResponse);
		System.out.println(oAuthToken.getAccess_token());

		// 액세스 토큰을 사용하여 사용자 정보 요청
		ResponseEntity<String> userInfoResponse = client.requestUserInfo(oAuthToken.getAccess_token());

		// 응답에서 사용자 정보를 파싱
		KaKaoUser kaKaoUser = client.getUserInfo(userInfoResponse);

		// 다른 로그인 방식을 사용한 계정이 있는지 확인
		if (memberRepository.existsByEmailAndLoginTypeNot(kaKaoUser.toMember().getEmail(), LoginType.KAKAO)) {
			Member member = memberRepository.findByEmail(kaKaoUser.toMember().getEmail())
					.orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

			// 탈퇴한(inactive) 회원이면 기존 정보 삭제
			if (member.getInactive() != null) {
				refreshTokenRepository.deleteByMemberId(member.getId());
				memberRepository.deleteById(member.getId());
				entityManager.flush();
			}
			else {
				throw new GeneralException(ErrorStatus._MEMBER_EMAIL_EXIST);
			}
		}


		// 사용자가 이미 존재하는지 확인
		Boolean isSpotMember = false;
		if (memberRepository.existsByEmail(kaKaoUser.toMember().getEmail())) {
			// 존재하는 경우, 사용자 정보를 가져옴
			Member member = memberRepository.findByEmail(kaKaoUser.toMember().getEmail())
					.orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

			updateMemberProfileImage(member, kaKaoUser);

			// 탈퇴한(inactive) 회원이면 기존 정보 삭제
			if (member.getInactive() != null) {
				refreshTokenRepository.deleteByMemberId(member.getId());
				memberRepository.deleteById(member.getId());
				entityManager.flush();
			}
			else {
				// JWT 토큰 생성
				TokenResponseDTO.TokenDTO token = jwtTokenProvider.createToken(member.getId());

				saveRefreshToken(member, token);

				if (isMemberExistsByCheckList(member)) {
					isSpotMember = true;
				}

				// 로그인 DTO 반환
				MemberResponseDTO.MemberSignInDTO memberSignInDto = MemberResponseDTO.MemberSignInDTO.builder()
						.tokens(token)
						.memberId(member.getId())
						.loginType(member.getLoginType())
						.email(member.getEmail())
						.build();

				return MemberResponseDTO.SocialLoginSignInDTO.toDTO(isSpotMember, memberSignInDto);
			}
		}

		// 존재하지 않는 경우, 새로운 회원 정보 저장
		Member member = memberRepository.save(kaKaoUser.toMember());

		// JWT 토큰 생성
		TokenResponseDTO.TokenDTO token = jwtTokenProvider.createToken(member.getId());

		saveRefreshToken(member, token);

		// 회원 가입 DTO 반환
		MemberResponseDTO.MemberSignInDTO dto = MemberResponseDTO.MemberSignInDTO.builder()
				.tokens(token)
				.memberId(member.getId())
				.loginType(member.getLoginType())
				.email(member.getEmail())
				.build();
		return MemberResponseDTO.SocialLoginSignInDTO.toDTO(isSpotMember, dto);
	}


	/**
	 * redirectURL을 반환합니다.
	 * @throws IOException URL 리다이렉트 중 발생하는 예외
	 */
	@Override
	public void redirectURL() throws IOException {
		// 카카오 OAuth 서비스에서 리다이렉트 URL 반환
		response.sendRedirect(client.getOauthRedirectURL());
	}

	/**
	 * 리프레시 토큰을 DB에 저장합니다.
	 * @param member 리프레시 토큰을 발급한 회원 정보
	 * @param token 발급된 토큰 정보
	 */
	private void saveRefreshToken(Member member, TokenResponseDTO.TokenDTO token) {
		// 기존 리프레시 토큰 삭제
		if (refreshTokenRepository.existsByMemberId(member.getId()))
			refreshTokenRepository.deleteAllByMemberId(member.getId());

		// DB에 저장하기 위한 새로운 리프레시 토큰 객체 생성
		RefreshToken refreshToken = RefreshToken.builder()
				.memberId(member.getId())
				.token(token.getRefreshToken())
				.build();

		// 리프레시 토큰 저장
		refreshTokenRepository.save(refreshToken);
	}

	public boolean isMemberExistsByCheckList(Member member) {
		Long memberId = member.getId();
		return memberThemeRepository.existsByMemberId(memberId) &&
				preferredRegionRepository.existsByMemberId(memberId) &&
				studyJoinReasonRepository.existsByMemberId(memberId);
	}
}

