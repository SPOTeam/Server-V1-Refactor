package com.example.spot.auth.application.refactor;

import com.example.spot.auth.presentation.dto.token.TokenResponseDTO.TokenDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public interface TokenProvider {

    TokenDTO createToken(Long memberId);

    TokenDTO reissueToken(String refreshToken);

    boolean isTokenExpired(String token);

    boolean validateToken(String token);

    Authentication getAuthentication(String token, UserDetails userDetails);

    String resolveToken(HttpServletRequest request);

    Long getMemberIdByToken(String token);
}
