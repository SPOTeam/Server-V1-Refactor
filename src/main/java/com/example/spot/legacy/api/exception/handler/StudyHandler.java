package com.example.spot.legacy.api.exception.handler;

import com.example.spot.legacy.api.code.status.ErrorStatus;
import com.example.spot.legacy.api.exception.GeneralException;

public class StudyHandler extends GeneralException {

    public StudyHandler(ErrorStatus status){
        super(status);
    }


}
