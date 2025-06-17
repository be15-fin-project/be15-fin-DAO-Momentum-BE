package com.dao.momentum.announcement.exception;

import com.dao.momentum.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class FileUploadFailedException extends RuntimeException {
    private final ErrorCode errorCode;

    public FileUploadFailedException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
