package com.example.spot.auth.application;

import java.io.IOException;

import com.example.spot.member.presentation.dto.MemberResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface KakaoAuthService {

	MemberResponseDTO.SocialLoginSignInDTO signUpByKAKAO(String code) throws JsonProcessingException;

	MemberResponseDTO.SocialLoginSignInDTO signUpByKAKAOForTest(String code)
			throws JsonProcessingException;

	void redirectURL() throws IOException;


}
