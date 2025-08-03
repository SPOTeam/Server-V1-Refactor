package com.example.spot.common.api.exception.base;

public class ExternalApiException extends RuntimeException {
    public ExternalApiException(String s, Exception e) {
        super(s, e);
    }
}
