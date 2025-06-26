package com.dao.momentum.organization.company.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.organization.company.query.dto.request.HolidaySearchRequest;
import com.dao.momentum.organization.company.query.dto.response.HolidayGetResponse;
import com.dao.momentum.organization.company.query.service.HolidayQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/holiday")
@Tag(name = "휴일", description = "휴일 관련 Query API")
public class HolidayQueryController {
    private final HolidayQueryService holidayQueryService;

    @Operation(summary="회사 휴일 조회", description = "사원은 회사의 휴일을 조회할 수 있다.")
    @GetMapping
    public ResponseEntity<ApiResponse<HolidayGetResponse>> getHolidays(
            @RequestBody HolidaySearchRequest request
            ) {
        HolidayGetResponse response = holidayQueryService.getHolidays(request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
