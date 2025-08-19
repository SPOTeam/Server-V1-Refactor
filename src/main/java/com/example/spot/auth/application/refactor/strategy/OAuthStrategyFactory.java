package com.example.spot.auth.application.refactor.strategy;

import com.example.spot.auth.exception.UnsupportedSocialLoginTypeException;
import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.member.domain.enums.LoginType;
import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OAuthStrategyFactory {

    private final Map<LoginType, OAuthStrategy> strategyMap;

    @PostConstruct
    void logRegistered() {
        log.info("Registered OAuth strategies: {}", strategyMap.keySet());
    }

    public OAuthStrategyFactory(List<OAuthStrategy> strategies) {
        this.strategyMap = Collections.unmodifiableMap(
                strategies.stream().collect(
                        Collectors.toMap(
                                OAuthStrategy::getType,
                                s -> s,
                                (a, b) -> {
                                    throw new IllegalStateException("중복된 OAuth Strategy가 주입되었습니다. :" + a.getType());
                                },
                                () -> new EnumMap<>(LoginType.class)
                        )
                )
        );
    }

    public OAuthStrategy getStrategy(LoginType type) {
        return Optional.ofNullable(strategyMap.get(type))
                .orElseThrow(() -> new UnsupportedSocialLoginTypeException(ErrorStatus._MEMBER_UNSUPPORTED_LOGIN_TYPE));
    }
}
