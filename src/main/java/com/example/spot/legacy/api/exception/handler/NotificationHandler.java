package com.example.spot.legacy.api.exception.handler;

import com.example.spot.legacy.api.code.status.ErrorStatus;
import com.example.spot.legacy.api.exception.GeneralException;

public class NotificationHandler extends GeneralException {

    public NotificationHandler(ErrorStatus code) {
        super(code);
    }
}
