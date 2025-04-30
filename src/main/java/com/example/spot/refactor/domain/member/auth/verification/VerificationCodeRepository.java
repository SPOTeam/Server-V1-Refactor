package com.example.spot.refactor.domain.member.auth.verification;

import com.example.spot.refactor.domain.member.auth.VerificationCode;
import com.example.spot.refactor.web.dto.token.TokenResponseDTO;

public interface VerificationCodeRepository {

    void addVerificationCode(String email, String code);

    VerificationCode getVerificationCode(String email);

    void setTempToken(TokenResponseDTO.TempTokenDTO tempTokenDTO, VerificationCode existingCode);
}
