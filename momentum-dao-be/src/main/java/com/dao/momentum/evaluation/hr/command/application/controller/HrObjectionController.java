package com.dao.momentum.evaluation.hr.command.application.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrObjectionCreateDto;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrObjectionCreateRequest;
import com.dao.momentum.evaluation.hr.command.application.dto.response.HrObjectionCreateResponse;
import com.dao.momentum.evaluation.hr.command.application.service.HrObjectionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hr-objections")
public class HrObjectionController {

    private final HrObjectionService hrObjectionService;

    @Operation(summary = "인사 평가 이의제기 제출", description = "사원이 인사 평가 결과에 대해 이의제기를 제출합니다.")
    @PostMapping("/{evaluationId}")
    public ApiResponse<HrObjectionCreateResponse> submitObjection(
            @PathVariable("evaluationId") Long evaluationId,
            @Valid @RequestBody HrObjectionCreateRequest request,
            @AuthenticationPrincipal UserDetails user
    ) {
        Long empId = Long.parseLong(user.getUsername());

        HrObjectionCreateDto dto = request.toDto(evaluationId, empId);

        HrObjectionCreateResponse response = hrObjectionService.create(dto);
        return ApiResponse.success(response);
    }
}
