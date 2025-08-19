package com.example.spot.common.infrastructure.feign.retry;

import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class SelectiveRetryErrorDecoder implements ErrorDecoder {

    public static final String GET = "GET";
    public static final String HEAD = "HEAD";
    public static final String OPTIONS = "OPTIONS";
    public static final String RETRYABLE_STATUS = "retryable status ";
    public static final String RETRY_AFTER = "Retry-After";
    public static final long THRESHOLD = 1000L;
    
    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();

        // 재시도 대상 상태코드
        boolean retryableStatus = status == 429 || status == 408 || (status >= 500 && status <= 599);

        // 멱등 메서드만 재시도 (GET/HEAD/OPTIONS)
        String method = response.request().httpMethod().name();
        boolean idempotent = method.equals(GET) || method.equals(HEAD) || method.equals(OPTIONS);
        if (!(retryableStatus && idempotent)) {
            return defaultDecoder.decode(methodKey, response);
        }

        Long retryAfter = extractRetryAfter(response.headers());
        return new RetryableException(status, RETRYABLE_STATUS + status, response.request().httpMethod(),
                retryAfter, response.request(), readBody(response), response.headers());
    }

    private Long extractRetryAfter(Map<String, Collection<String>> headers) {
        Collection<String> value = headers.getOrDefault(RETRY_AFTER, Collections.emptyList());
        if (value.isEmpty()) {
            return null;
        }
        String v = value.iterator().next().trim();
        try {
            long seconds = Long.parseLong(v);
            return System.currentTimeMillis() + (seconds * THRESHOLD);
        } catch (NumberFormatException ignore) {
            return null;
        }
    }

    private byte[] readBody(Response response) {
        if (response.body() == null) {
            return null;
        }
        try (InputStream in = response.body().asInputStream()) {
            return in.readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }
}