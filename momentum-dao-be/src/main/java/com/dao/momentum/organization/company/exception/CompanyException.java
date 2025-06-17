package com.dao.momentum.organization.company.exception;

import com.dao.momentum.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CompanyException extends RuntimeException {
    private final ErrorCode errorCode;

    public CompanyException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
