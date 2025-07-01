package com.dao.momentum.approve.query.controller;

import com.dao.momentum.approve.query.dto.response.DayoffResponse;
import com.dao.momentum.approve.query.dto.response.RefreshResponse;
import com.dao.momentum.approve.query.service.RemainingVacationQueryService;
import com.dao.momentum.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employees")
@Tag(name = "잔여 휴가 시간 조회", description ="잔여 휴가 시간 조회 API")
@RequiredArgsConstructor
public class RemainingVacationQueryController {

    private final RemainingVacationQueryService remainingVacationQueryService;

    /* 잔여 연차 시간 조회 */
    @GetMapping("/{empId}/dayoff")
    @Operation(summary = "잔여 연차 시간 조회", description ="사원의 잔여 연차 시간을 조회합니다.")
    public ResponseEntity<ApiResponse<DayoffResponse>> getRemainingDayoffs(
            @PathVariable long empId
    ) {
        DayoffResponse response = remainingVacationQueryService.getRemainingDayoffs(empId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /* 잔여 리프레시 휴가 일수 조회*/
    @GetMapping("/{empId}/refresh")
    @Operation(summary = "잔여 리프레시 휴가 일수 조회", description ="사원의 잔여 리프레시 휴가 일수를 조회합니다.")
    public ResponseEntity<ApiResponse<RefreshResponse>> getRemainingRefreshs(
            @PathVariable long empId
    ) {
        RefreshResponse response = remainingVacationQueryService.getRemainingRefreshs(empId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
