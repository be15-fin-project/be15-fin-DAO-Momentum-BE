package com.dao.momentum.approve.exception;

import com.dao.momentum.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class NotExistTabException extends RuntimeException {
    private final ErrorCode errorCode;

    public NotExistTabException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
