package com.example.spot.legacy.api.exception.handler;

import com.example.spot.legacy.api.code.status.ErrorStatus;
import com.example.spot.legacy.api.exception.GeneralException;

public class PostHandler extends GeneralException {

    public PostHandler(ErrorStatus code) {
        super(code);
    }
}
