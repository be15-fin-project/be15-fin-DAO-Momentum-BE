package com.dao.momentum.approve.exception;

import com.dao.momentum.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class OcrRequestFailedException extends RuntimeException {
    private final ErrorCode errorCode;

    public OcrRequestFailedException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
