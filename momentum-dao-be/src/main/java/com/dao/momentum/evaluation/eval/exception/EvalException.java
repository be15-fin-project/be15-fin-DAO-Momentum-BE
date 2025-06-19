package com.dao.momentum.evaluation.eval.exception;

import com.dao.momentum.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class EvalException extends RuntimeException {
    private final ErrorCode errorCode;

    public EvalException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
