package com.dao.momentum.evaluation.hr.exception;

import com.dao.momentum.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class HrException extends RuntimeException {
    private final ErrorCode errorCode;

    public HrException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
