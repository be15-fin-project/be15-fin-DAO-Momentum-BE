package com.dao.momentum.announcement.exception;

import com.dao.momentum.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class AnnouncementAccessDeniedException extends RuntimeException {
    private final ErrorCode errorCode;

    public AnnouncementAccessDeniedException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
