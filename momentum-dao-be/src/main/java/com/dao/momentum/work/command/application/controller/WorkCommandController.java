package com.dao.momentum.work.command.application.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.work.command.application.dto.request.WorkStartRequest;
import com.dao.momentum.work.command.application.dto.response.WorkCreateResponse;
import com.dao.momentum.work.command.application.service.WorkCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/works")
@RequiredArgsConstructor
@Tag(name = "근태 관리", description = "출퇴근 기록 등록, 수정, 삭제 API")
public class WorkCommandController {
    private final WorkCommandService workCommandService;

    @PostMapping
    @Operation(summary = "출근 등록", description = "사원이 출근 버튼을 클릭하여 출근을 등록합니다.")
    public ResponseEntity<ApiResponse<WorkCreateResponse>> createWork(
            @RequestBody WorkStartRequest workStartRequest, HttpServletRequest httpServletRequest
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                    workCommandService.createWork(workStartRequest, httpServletRequest)
                )
        );
    }


}