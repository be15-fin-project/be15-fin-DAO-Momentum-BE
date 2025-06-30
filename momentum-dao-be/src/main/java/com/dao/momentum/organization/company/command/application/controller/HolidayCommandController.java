package com.dao.momentum.organization.company.command.application.controller;

import com.dao.momentum.common.dto.ApiResponse;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.company.command.application.dto.request.HolidayCrateRequest;
import com.dao.momentum.organization.company.command.application.dto.response.HolidayCreateResponse;
import com.dao.momentum.organization.company.command.application.dto.response.HolidayDeleteResponse;
import com.dao.momentum.organization.company.command.application.service.HolidayCommandService;
import com.dao.momentum.organization.company.exception.CompanyException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/holiday")
@Tag(name = "휴일", description = "휴일 관련 Command API")
public class HolidayCommandController {
    private final HolidayCommandService holidayCommandService;

    @Operation(summary="회사 휴일 등록", description = "관리자는 회사의 휴일을 등록할 수 있다.")
    @PostMapping
    public ResponseEntity<ApiResponse<HolidayCreateResponse>> createHoliday(
            @RequestBody @Valid HolidayCrateRequest request
    ){
        HolidayCreateResponse response = holidayCommandService.createHoliday(request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary="회사 휴일 삭제", description = "관리자는 회사의 휴일을 삭제할 수 있다.")
    @DeleteMapping("/{holidayId}")
    public ResponseEntity<ApiResponse<HolidayDeleteResponse>> deleteHoliday(
            @PathVariable Integer holidayId
    ){
        HolidayDeleteResponse response = holidayCommandService.deleteHoliday(holidayId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @ExceptionHandler(CompanyException.class)
    public ResponseEntity<ApiResponse<Void>> handleCompanyException(CompanyException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(errorCode.getCode(), errorCode.getMessage()));
    }
}
