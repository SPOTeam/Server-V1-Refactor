package com.example.spot.legacy.api.exception.handler;

import com.example.spot.legacy.api.code.status.ErrorStatus;
import com.example.spot.legacy.api.exception.GeneralException;

public class S3Handler extends GeneralException {

    public S3Handler(ErrorStatus code) {
        super(code);
    }
}