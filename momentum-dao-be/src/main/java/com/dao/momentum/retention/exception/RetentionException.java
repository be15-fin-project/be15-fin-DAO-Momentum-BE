package com.dao.momentum.retention.exception;

import com.dao.momentum.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class RetentionException extends RuntimeException {
    private final ErrorCode errorCode;

    public RetentionException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
