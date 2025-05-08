package com.example.spot.common.api.exception.handler;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.GeneralException;

public class NotificationHandler extends GeneralException {

    public NotificationHandler(ErrorStatus code) {
        super(code);
    }
}
