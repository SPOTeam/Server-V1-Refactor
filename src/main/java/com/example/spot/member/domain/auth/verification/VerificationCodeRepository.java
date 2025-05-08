package com.example.spot.member.domain.auth.verification;

import com.example.spot.member.domain.auth.VerificationCode;
import com.example.spot.member.presentation.dto.token.TokenResponseDTO;

public interface VerificationCodeRepository {

    void addVerificationCode(String email, String code);

    VerificationCode getVerificationCode(String email);

    void setTempToken(TokenResponseDTO.TempTokenDTO tempTokenDTO, VerificationCode existingCode);
}
