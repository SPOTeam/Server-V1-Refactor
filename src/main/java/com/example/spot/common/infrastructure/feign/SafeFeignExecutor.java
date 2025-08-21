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
            String message = e.getMessage() != null ? e.getMessage() : "";
            String body = e.contentUTF8();
            String masked = mask(message);
            throw new ExternalApiException(
                    "Feign API 호출 실패(" + e.status() + "): " + masked, body, e
            );
        }
    }

    private static String mask(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        String out = s;
        out = out.replaceAll("(?i)(Authorization)\\s*[:=]\\s*([^\\r\\n]+)", "$1: [REDACTED]");
        out = out.replaceAll("(?i)(Set-Cookie|Cookie)\\s*[:=]\\s*([^\\r\\n]+)", "$1: [REDACTED]");
        out = out.replaceAll("(?i)(access[_-]?token|id[_-]?token|refresh[_-]?token)\\s*[:=]\\s*([\\w\\.-]+)",
                "$1=[REDACTED]");
        out = out.replaceAll("(?i)(\"(?:password|pass|secret|token|authorization)\"\\s*:\\s*\")([^\"]+)(\")",
                "$1[REDACTED]$3");
        return out;
    }
}