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
    POSITION_ALREADY_EXISTS("10004","이미 존재하는 직위입니다.",HttpStatus.FORBIDDEN),

    // 회사 오류 (11001 - 11999)
    COMPANY_INFO_NOT_FOUND("11001", "시스템 오류입니다.", HttpStatus.NOT_FOUND),

    // 출퇴근 오류
    WORKTYPE_NOT_FOUND("20001", "시스템 오류입니다.", HttpStatus.NOT_FOUND),
    IP_NOT_ALLOWED("20002", "출퇴근 등록이 불가능한 IP입니다.", HttpStatus.BAD_REQUEST),
    INVALID_WORK_TIME("20003", "유효하지 않은 출퇴근 요청입니다.", HttpStatus.BAD_REQUEST),
    WORK_REQUESTED_ON_HOLIDAY("20004", "승인 없는 주말/휴일 출근은 불가능합니다.", HttpStatus.BAD_REQUEST),
    WORK_ALREADY_RECORDED("20005", "이미 출근 등록된 날짜입니다.", HttpStatus.BAD_REQUEST),
    ACCEPTED_WORK_ALREADY_RECORDED("20006", "재택근무/휴가/출장이 승인된 날짜입니다.", HttpStatus.BAD_REQUEST),
    WORKING_52H_NOT_ALLOWED("20007", "주 52시간 이상의 근무는 불가능합니다.", HttpStatus.BAD_REQUEST),
    WORK_NOT_FOUND("20008", "시스템 오류입니다." ,HttpStatus.NOT_FOUND),

    // 결재 오류 (30001 ~ 39999)
    NOT_EXIST_TAB("30001", "존재하지 않는 결재 탭입니다.", HttpStatus.BAD_REQUEST),
    
    //공지사항 오류(60001 - 69999)
    ANNOUNCEMENT_NOT_FOUND("60001", "해당 공지사항 게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ANNOUNCEMENT_NOT_AUTHOR("60002", "해당 공지사항 게시글의 작성자가 아닙니다.", HttpStatus.FORBIDDEN),

    // 파일 처리 오류(80001 - 89999)
    FILE_UPLOAD_FAIL("80001", "파일 업로드에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),

    // 공통 오류
    VALIDATION_ERROR("90001", "입력 값 검증 오류입니다.", HttpStatus.BAD_REQUEST),
    UNKNOWN_RUNTIME_ERROR("90002", "알 수 없는 런타임 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    UNKNOWN_ERROR("90003", "알 수 없는 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
