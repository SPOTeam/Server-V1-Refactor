package com.example.spot.common.api.exception.handler;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.GeneralException;

public class MemberHandler extends GeneralException {

    public MemberHandler(ErrorStatus code) {
        super(code);
    }
}
