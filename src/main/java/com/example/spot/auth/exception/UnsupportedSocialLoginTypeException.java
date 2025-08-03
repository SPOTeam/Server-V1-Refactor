package com.example.spot.auth.exception;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.base.UnsupportedException;

public class UnsupportedSocialLoginTypeException extends UnsupportedException {

    public UnsupportedSocialLoginTypeException(ErrorStatus status) {
        super(status);
    }
}
