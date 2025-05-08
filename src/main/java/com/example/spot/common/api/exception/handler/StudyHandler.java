package com.example.spot.common.api.exception.handler;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.GeneralException;

public class StudyHandler extends GeneralException {

    public StudyHandler(ErrorStatus status){
        super(status);
    }


}
