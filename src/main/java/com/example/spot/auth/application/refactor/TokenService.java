package com.example.spot.auth.application.refactor;

import com.example.spot.auth.presentation.dto.token.TokenResponseDTO;

public interface TokenService {

	// 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급
	TokenResponseDTO.TokenDTO reissueToken(String refreshToken);
}
