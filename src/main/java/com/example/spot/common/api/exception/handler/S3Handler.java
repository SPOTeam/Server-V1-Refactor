package com.example.spot.common.api.exception.handler;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.GeneralException;

public class S3Handler extends GeneralException {

    public S3Handler(ErrorStatus code) {
        super(code);
    }
}