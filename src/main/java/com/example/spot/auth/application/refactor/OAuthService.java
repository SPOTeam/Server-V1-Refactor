package com.example.spot.auth.application.refactor;

import com.example.spot.auth.application.refactor.impl.OAuthMemberProcessor;
import com.example.spot.auth.application.refactor.strategy.OAuthStrategy;
import com.example.spot.auth.application.refactor.strategy.OAuthStrategyFactory;
import com.example.spot.member.domain.enums.LoginType;
import com.example.spot.member.presentation.dto.MemberResponseDTO.SocialLoginSignInDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final OAuthStrategyFactory strategyFactory;
    private final OAuthMemberProcessor memberProcessor;

    public String redirectURL(LoginType type) {
        return strategyFactory.getStrategy(type).getOauthRedirectURL();
    }

    public SocialLoginSignInDTO loginOrSignUp(LoginType type, String code) {
        OAuthStrategy strategy = strategyFactory.getStrategy(type);
        return memberProcessor.processOAuthMember(type, strategy.toMember(code));
    }
}
