package com.dao.momentum.work.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.work.query.dto.request.AdminWorkSearchRequest;
import com.dao.momentum.work.query.dto.request.WorkSearchRequest;
import com.dao.momentum.work.query.dto.response.WorkListResponse;
import com.dao.momentum.work.query.service.WorkQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping
    @Operation(summary = "관리자 출퇴근 조회", description = "관리자가 특정 사원의 출퇴근 내역을 조회합니다.")
    public ResponseEntity<ApiResponse<WorkListResponse>> getWorks(
            @ModelAttribute AdminWorkSearchRequest adminWorkSearchRequest
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        workQueryService.getWorks(adminWorkSearchRequest)
                )
        );
    }



}