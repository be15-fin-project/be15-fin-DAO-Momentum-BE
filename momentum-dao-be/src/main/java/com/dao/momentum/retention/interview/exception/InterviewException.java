package com.dao.momentum.retention.interview.exception;

import com.dao.momentum.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class InterviewException extends RuntimeException {
    private final ErrorCode errorCode;

    public InterviewException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
