package com.dao.momentum.announcement.exception;

import com.dao.momentum.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class NoSuchAnnouncementException extends RuntimeException {
    private final ErrorCode errorCode;

    public NoSuchAnnouncementException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
