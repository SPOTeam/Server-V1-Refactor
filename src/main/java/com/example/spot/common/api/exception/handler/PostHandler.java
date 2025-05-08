package com.example.spot.common.api.exception.handler;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.GeneralException;

public class PostHandler extends GeneralException {

    public PostHandler(ErrorStatus code) {
        super(code);
    }
}
