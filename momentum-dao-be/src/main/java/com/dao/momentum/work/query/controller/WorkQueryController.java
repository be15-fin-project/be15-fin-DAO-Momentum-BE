package com.dao.momentum.work.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.work.query.dto.request.AdminWorkSearchRequest;
import com.dao.momentum.work.query.dto.request.WorkSearchRequest;
import com.dao.momentum.work.query.dto.response.AttendanceResponse;
import com.dao.momentum.work.query.dto.response.WorkDetailsResponse;
import com.dao.momentum.work.query.dto.response.WorkListResponse;
import com.dao.momentum.work.query.service.WorkQueryService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/works")
@RequiredArgsConstructor
@Tag(name = "근태 조회", description = "출퇴근 기록 조회 API")
public class WorkQueryController {

    private final WorkQueryService workQueryService;

    @GetMapping("/me")
    @Operation(summary = "출퇴근 조회", description = "사원이 자신의 출퇴근 내역을 조회합니다.")
    public ResponseEntity<ApiResponse<WorkListResponse>> getMyWorks(
            @AuthenticationPrincipal UserDetails userDetails, @ModelAttribute WorkSearchRequest workSearchRequest
            ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        workQueryService.getMyWorks(userDetails, workSearchRequest)
                )
        );
    }

    @GetMapping("/me/today")
    @Operation(summary = "당일 출근 여부 조회", description = "사원이 자신의 출근 등록 여부를 조회합니다.")
    public ResponseEntity<ApiResponse<AttendanceResponse>> getMyTodaysAttendance(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        LocalDate today = LocalDate.now();

        return ResponseEntity.ok(
                ApiResponse.success(
                        workQueryService.getMyTodaysAttendance(userDetails, today)
                )
        );
    }

    @Hidden
    @GetMapping("/me/today/test") // 개발자 테스트용
    public ResponseEntity<ApiResponse<AttendanceResponse>> getMyTodaysAttendanceTest(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) LocalDate today
    ) {
        if (today == null) {
            today = LocalDate.now();
        }

        return ResponseEntity.ok(
                ApiResponse.success(
                        workQueryService.getMyTodaysAttendance(userDetails, today)
                )
        );
    }

    @GetMapping
    @Operation(summary = "관리자 출퇴근 조회", description = "관리자가 회사에 등록된 출퇴근 내역을 조회합니다.")
    public ResponseEntity<ApiResponse<WorkListResponse>> getWorks(
            @ModelAttribute AdminWorkSearchRequest adminWorkSearchRequest
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        workQueryService.getWorks(adminWorkSearchRequest)
                )
        );
    }

    @GetMapping("/{workId}")
    @Operation(summary = "관리자 출퇴근 상세 조회", description = "관리자가 회사에 등록된 출퇴근 항목의 세부 내용을 조회합니다.")
    public ResponseEntity<ApiResponse<WorkDetailsResponse>> getWorkDetails(
            @PathVariable long workId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        workQueryService.getWorkDetails(workId)
                )
        );
    }


}