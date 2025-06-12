package com.dao.momentum.common.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum ErrorCode {
    //사원 오류(10001 - 199999)
    EMPLOYEE_ALREADY_EXISTS("10001", "해당 사원이 이미 존재합니다.",HttpStatus.BAD_REQUEST),
    EMPLOYEE_NOT_FOUND("10002","해당 사원을 찾을 수 없습니다.",HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS("10003","유효하지 않은 입력입니다. 다시 입력해주세요",HttpStatus.FORBIDDEN),

    // 공통 오류
    VALIDATION_ERROR("90001", "입력 값 검증 오류입니다.", HttpStatus.BAD_REQUEST),
    UNKNOWN_RUNTIME_ERROR("90002", "알 수 없는 런타임 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    UNKNOWN_ERROR("90003", "알 수 없는 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
