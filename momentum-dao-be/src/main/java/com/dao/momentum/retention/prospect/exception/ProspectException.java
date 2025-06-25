package com.dao.momentum.retention.prospect.exception;

import com.dao.momentum.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ProspectException extends RuntimeException {
    private final ErrorCode errorCode;

    public ProspectException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
