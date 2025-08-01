package com.example.spot.common.infrastructure.feign;

import com.example.spot.common.api.exception.base.ExternalApiException;
import feign.FeignException;
import java.util.function.Supplier;

public final class SafeFeignExecutor {

    private SafeFeignExecutor() {
    }

    public static <T> T run(Supplier<T> call) {
        try {
            return call.get();
        } catch (FeignException e) {
            throw new ExternalApiException(
                    "Feign API 호출 실패: " + extractMessage(e), e);
        }
    }

    private static String extractMessage(FeignException e) {
        return e.responseBody()
                .map(body -> new String(body.array()))  // byte[] → String
                .orElse(e.getMessage());
    }
}