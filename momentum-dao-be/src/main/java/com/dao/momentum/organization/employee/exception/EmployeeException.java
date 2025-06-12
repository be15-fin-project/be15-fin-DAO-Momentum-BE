package com.dao.momentum.organization.employee.exception;

import com.dao.momentum.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class EmployeeException extends RuntimeException {
    private final ErrorCode errorCode;

    public EmployeeException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
