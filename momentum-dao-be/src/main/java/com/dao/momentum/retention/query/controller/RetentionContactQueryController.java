package com.dao.momentum.retention.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.retention.query.dto.request.RetentionContactListRequestDto;
import com.dao.momentum.retention.query.dto.response.RetentionContactListResultDto;
import com.dao.momentum.retention.query.service.RetentionContactQueryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/retention/contact")
public class RetentionContactQueryController {

    private final RetentionContactQueryService service;

    @GetMapping
    @Operation(summary = "면담 기록 목록 조회", description = "전체 사원 면담 기록을 필터 조건으로 조회")
    public ApiResponse<RetentionContactListResultDto> getContactList(
            @ModelAttribute RetentionContactListRequestDto req) {
        return ApiResponse.success(service.getContactList(req));
    }

    @GetMapping("/my")
    @Operation(summary = "나에게 요청된 면담 기록 조회", description = "로그인 사용자가 상급자인 면담 기록만 조회")
    public ApiResponse<RetentionContactListResultDto> getMyContactRequests(
            @ModelAttribute RetentionContactListRequestDto req,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long empId = Long.parseLong(userDetails.getUsername());
        return ApiResponse.success(service.getMyRequestedContactList(empId, req));
    }
}
