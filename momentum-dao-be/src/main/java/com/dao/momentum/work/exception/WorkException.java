package com.dao.momentum.work.exception;

import com.dao.momentum.common.exception.ErrorCode;

public class WorkException extends RuntimeException {
    ErrorCode errorCode;

    public WorkException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
