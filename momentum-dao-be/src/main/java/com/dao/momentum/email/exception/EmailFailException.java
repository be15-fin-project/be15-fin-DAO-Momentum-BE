package com.dao.momentum.email.exception;

import com.dao.momentum.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class EmailFailException extends RuntimeException {
    private final ErrorCode errorCode;

    public EmailFailException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
