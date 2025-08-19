package com.example.spot.common.feign;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spot.common.infrastructure.feign.retry.SelectiveRetryErrorDecoder;
import feign.Request;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SelectiveRetryErrorDecoderTest {

    private final ErrorDecoder decoder = new SelectiveRetryErrorDecoder();

    private Response newResponse(int status, String method, Map<String, Collection<String>> headers, String body) {
        Request req = Request.create(
                Request.HttpMethod.valueOf(method),
                "https://api.test/resource",
                Map.of(), // request headers
                null,     // body
                StandardCharsets.UTF_8,
                null
        );

        Response.Builder builder = Response.builder()
                .status(status)
                .reason("test")
                .headers(headers == null ? Map.of() : headers)
                .request(req);

        if (body != null) {
            builder.body(body.getBytes(StandardCharsets.UTF_8));
        }

        return builder.build();
    }

    @Test
    void GET_500_is_retryable() {
        Response res = newResponse(500, "GET", null, "{\"msg\":\"oops\"}");
        Exception ex = decoder.decode("key", res);
        assertThat(ex).isInstanceOf(RetryableException.class);
    }

    @Test
    void GET_429_with_retryAfter_is_retryable() {
        Map<String, Collection<String>> headers = Map.of("Retry-After", List.of("2"));
        Response res = newResponse(429, "GET", headers, null);
        Exception ex = decoder.decode("key", res);
        assertThat(ex).isInstanceOf(RetryableException.class);
    }

    @Test
    void POST_500_is_NOT_retryable() {
        Response res = newResponse(500, "POST", null, null);
        Exception ex = decoder.decode("key", res);
        assertThat(ex).isNotInstanceOf(RetryableException.class);
    }

    @Test
    void GET_400_is_NOT_retryable() {
        Response res = newResponse(400, "GET", null, null);
        Exception ex = decoder.decode("key", res);
        assertThat(ex).isNotInstanceOf(RetryableException.class);
    }
}