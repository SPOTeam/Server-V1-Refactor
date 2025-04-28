package com.example.spot.legacy.repository.verification;

import com.example.spot.legacy.domain.auth.VerificationCode;
import com.example.spot.legacy.web.dto.token.TokenResponseDTO;

public interface VerificationCodeRepository {

    void addVerificationCode(String email, String code);

    VerificationCode getVerificationCode(String email);

    void setTempToken(TokenResponseDTO.TempTokenDTO tempTokenDTO, VerificationCode existingCode);
}
