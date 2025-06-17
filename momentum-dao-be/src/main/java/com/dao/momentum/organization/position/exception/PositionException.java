package com.dao.momentum.organization.position.exception;

import com.dao.momentum.common.exception.ErrorCode;

public class PositionException extends RuntimeException{
    private final ErrorCode errorCode;

    public PositionException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
