package com.example.spot.auth.application.legacy;

import com.example.spot.member.presentation.dto.MemberRequestDTO.SignUpDetailDTO;
import com.example.spot.auth.presentation.dto.rsa.Rsa;
import com.example.spot.member.presentation.dto.MemberRequestDTO;
import com.example.spot.member.presentation.dto.MemberResponseDTO;
import com.example.spot.member.presentation.dto.MemberResponseDTO.SocialLoginSignInDTO;
import com.example.spot.auth.presentation.dto.naver.NaverCallback;
import com.example.spot.auth.presentation.dto.naver.NaverOAuthToken;
import com.example.spot.auth.presentation.dto.token.TokenResponseDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Deprecated
public interface AuthService {

    MemberResponseDTO.MemberInfoCreationDTO signUpAndPartialUpdate(SignUpDetailDTO sign);

    MemberResponseDTO.InactiveMemberDTO withdraw();

    void authorizeWithNaver(HttpServletRequest request, HttpServletResponse response);

    SocialLoginSignInDTO signInWithNaver(HttpServletRequest request, HttpServletResponse response, NaverCallback naverCallback) throws Exception;

    SocialLoginSignInDTO signInWithNaver(HttpServletRequest request, HttpServletResponse response, NaverOAuthToken.NaverTokenIssuanceDTO naverTokenDTO) throws Exception;

    MemberResponseDTO.MemberSignInDTO signIn(Long httpSession, MemberRequestDTO.SignInDTO signInDTO) throws Exception;

    Rsa.RSAPublicKey getRSAPublicKey() throws Exception;

    void sendVerificationCode(HttpServletRequest request, HttpServletResponse response, String email);

    TokenResponseDTO.TempTokenDTO verifyEmail(String verificationCode, String email);

    MemberResponseDTO.MemberSignInDTO signUp(Long rsaId, MemberRequestDTO.SignUpDTO signUpDTO) throws Exception;

    MemberResponseDTO.FindIdDTO findId();

    MemberResponseDTO.FindPwDTO findPw(String loginId);

    MemberResponseDTO.AvailabilityDTO checkLoginIdAvailability(String loginId);

    MemberResponseDTO.AvailabilityDTO checkEmailAvailability(String email);

    MemberResponseDTO.CheckMemberDTO checkIsSpotMember(Long loginId);

    MemberResponseDTO.NicknameDuplicateDTO checkNicknameAvailability(String nickname);
}
