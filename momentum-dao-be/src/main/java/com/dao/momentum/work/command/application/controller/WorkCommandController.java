package com.dao.momentum.work.command.application.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.work.command.application.dto.request.WorkEndRequest;
import com.dao.momentum.work.command.application.dto.request.WorkStartRequest;
import com.dao.momentum.work.command.application.dto.response.WorkEndResponse;
import com.dao.momentum.work.command.application.dto.response.WorkStartResponse;
import com.dao.momentum.work.command.application.service.WorkCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/works")
@RequiredArgsConstructor
@Tag(name = "근태 관리", description = "출퇴근 기록 등록, 수정, 삭제 API")
public class WorkCommandController {
    private final WorkCommandService workCommandService;

    @PostMapping
    @Operation(summary = "출근 등록", description = "사원이 출근 버튼을 클릭하여 출근을 등록합니다.")
    public ResponseEntity<ApiResponse<WorkStartResponse>> createWork(
            @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest httpServletRequest
    ) {
        LocalDateTime startPushedAt = LocalDateTime.now();

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                    workCommandService.createWork(userDetails, httpServletRequest, startPushedAt)
                )
        );
    }

    @PutMapping
    @Operation(summary = "퇴근 등록", description = "사원이 출근 버튼을 클릭하여 출근을 등록합니다.")
    public ResponseEntity<ApiResponse<WorkEndResponse>> updateWork(
            @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest httpServletRequest
    ) {
        LocalDateTime endPushedAt = LocalDateTime.now();

        return ResponseEntity.ok(
                ApiResponse.success(
                        workCommandService.updateWork(userDetails, httpServletRequest, endPushedAt)
                )
        );
    }

    @PostMapping("/test")
    @Operation(summary = "출근 등록 테스트", description = "사원이 출근 버튼을 클릭하여 출근을 등록합니다. (당일이 아니어도 출근 등록 가능)")
    public ResponseEntity<ApiResponse<WorkStartResponse>> testCreateWork(
            @AuthenticationPrincipal UserDetails userDetails, @RequestBody WorkStartRequest workStartRequest, HttpServletRequest httpServletRequest
    ) {
        LocalDateTime startPushedAt = LocalDateTime.now();
        if (workStartRequest != null && workStartRequest.getStartPushedAt() != null) {
            startPushedAt = workStartRequest.getStartPushedAt();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        workCommandService.createWork(userDetails, httpServletRequest, startPushedAt)
                )
        );
    }

    @PutMapping("/test")
    @Operation(summary = "퇴근 등록 테스트", description = "사원이 출근 버튼을 클릭하여 출근을 등록합니다. (당일이 아니어도 퇴근 등록 가능)")
    public ResponseEntity<ApiResponse<WorkEndResponse>> testUpdateWork(
            @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest httpServletRequest, @RequestBody WorkEndRequest workEndRequest
    ) {
        LocalDateTime endPushedAt = LocalDateTime.now();
        if (workEndRequest != null && workEndRequest.getEndPushedAt() != null) {
            endPushedAt = workEndRequest.getEndPushedAt();
        }

        return ResponseEntity.ok(
                ApiResponse.success(
                        workCommandService.updateWork(userDetails, httpServletRequest, endPushedAt)
                )
        );
    }


}