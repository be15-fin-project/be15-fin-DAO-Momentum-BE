package com.dao.momentum.approve.exception;

import com.dao.momentum.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ReceiptApproveException extends RuntimeException {
    private final ErrorCode errorCode;

    public ReceiptApproveException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
