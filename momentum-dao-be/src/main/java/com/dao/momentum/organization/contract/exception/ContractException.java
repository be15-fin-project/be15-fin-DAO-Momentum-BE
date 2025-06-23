package com.dao.momentum.organization.contract.exception;

import com.dao.momentum.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ContractException extends RuntimeException {
    private final ErrorCode errorCode;

    public ContractException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
