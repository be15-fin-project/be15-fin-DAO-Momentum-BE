package com.dao.momentum.evaluation.exception;

import com.dao.momentum.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class KpiException extends RuntimeException {
    private final ErrorCode errorCode;

    public KpiException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
