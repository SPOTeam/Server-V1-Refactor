package com.example.spot.auth.domain.verification;

import com.example.spot.auth.domain.VerificationCode;
import com.example.spot.auth.presentation.dto.token.TokenResponseDTO;

public interface VerificationCodeRepository {

    void addVerificationCode(String email, String code);

    VerificationCode getVerificationCode(String email);

    void setTempToken(TokenResponseDTO.TempTokenDTO tempTokenDTO, VerificationCode existingCode);
}
