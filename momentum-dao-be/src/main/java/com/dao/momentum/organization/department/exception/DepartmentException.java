package com.dao.momentum.organization.department.exception;

import com.dao.momentum.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class DepartmentException extends RuntimeException {
    private final ErrorCode errorCode;

    public DepartmentException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
