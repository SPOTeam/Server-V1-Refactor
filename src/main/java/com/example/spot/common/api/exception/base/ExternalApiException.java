package com.example.spot.common.api.exception.base;

public class ExternalApiException extends RuntimeException {

    private final String responseBody;

    public ExternalApiException(String s, String body, Exception e) {
        super(s, e);
        this.responseBody = body;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
