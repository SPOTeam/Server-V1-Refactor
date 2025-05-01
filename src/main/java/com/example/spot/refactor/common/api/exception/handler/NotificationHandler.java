package com.example.spot.refactor.common.api.exception.handler;

import com.example.spot.refactor.common.api.code.status.ErrorStatus;
import com.example.spot.refactor.common.api.exception.GeneralException;

public class NotificationHandler extends GeneralException {

    public NotificationHandler(ErrorStatus code) {
        super(code);
    }
}
