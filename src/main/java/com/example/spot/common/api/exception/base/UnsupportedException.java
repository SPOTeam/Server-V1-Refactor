package com.example.spot.common.api.exception.base;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.GeneralException;


public class UnsupportedException extends GeneralException {

    public UnsupportedException(ErrorStatus status) {
        super(status);
    }
}
