package com.example.spot.legacy.api.exception.handler;

import com.example.spot.legacy.api.code.status.ErrorStatus;
import com.example.spot.legacy.api.exception.GeneralException;

public class MemberHandler extends GeneralException {

    public MemberHandler(ErrorStatus code) {
        super(code);
    }
}
