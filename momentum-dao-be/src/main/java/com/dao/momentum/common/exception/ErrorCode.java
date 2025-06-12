package com.dao.momentum.common.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum ErrorCode {
    // 출퇴근 오류
    WORKTYPE_NOT_FOUND("10001", "시스템 오류입니다.", HttpStatus.NOT_FOUND),
    IP_NOT_ALLOWED("10002", "출퇴근 등록이 불가능한 IP입니다.", HttpStatus.BAD_REQUEST),
    INVALID_WORK_TIME("10003", "유효하지 않은 출퇴근 요청입니다.", HttpStatus.BAD_REQUEST),
    WORK_REQUESTED_ON_HOLIDAY("10004", "승인 없는 주말/휴일 출근은 불가능합니다.", HttpStatus.BAD_REQUEST),
    WORK_ALREADY_RECORDED("10005", "이미 출근 등록된 날짜입니다.", HttpStatus.BAD_REQUEST),
    ACCEPTED_WORK_ALREADY_RECORDED("10006", "재택근무/휴가/출장이 승인된 날짜입니다.", HttpStatus.BAD_REQUEST),
    WORKING_52H_NOT_ALLOWED("10007", "주 52시간 이상의 근무는 불가능합니다.", HttpStatus.BAD_REQUEST),

    // 공통 오류
    VALIDATION_ERROR("90001", "입력 값 검증 오류입니다.", HttpStatus.BAD_REQUEST),
    UNKNOWN_RUNTIME_ERROR("90002", "알 수 없는 런타임 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    UNKNOWN_ERROR("90003", "알 수 없는 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
