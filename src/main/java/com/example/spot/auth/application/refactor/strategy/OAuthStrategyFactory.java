package com.example.spot.auth.application.refactor.strategy;

import com.example.spot.auth.exception.UnsupportedSocialLoginTypeException;
import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.member.domain.enums.LoginType;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OAuthStrategyFactory {

    private final Map<LoginType, OAuthStrategy> strategyMap;

    public OAuthStrategyFactory(List<OAuthStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(OAuthStrategy::getType, s -> s));
    }

    public OAuthStrategy getStrategy(LoginType type) {
        return Optional.ofNullable(strategyMap.get(type))
                .orElseThrow(() -> new UnsupportedSocialLoginTypeException(ErrorStatus._MEMBER_UNSUPPORTED_LOGIN_TYPE));
    }
}
