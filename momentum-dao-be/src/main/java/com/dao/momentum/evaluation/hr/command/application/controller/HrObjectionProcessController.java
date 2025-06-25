package com.dao.momentum.evaluation.hr.command.application.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrObjectionProcessRequestDto;
import com.dao.momentum.evaluation.hr.command.application.dto.response.HrObjectionProcessResponseDto;
import com.dao.momentum.evaluation.hr.command.application.facade.HrObjectionProcessFacade;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hr-objections")
public class HrObjectionProcessController {

    private final HrObjectionProcessFacade hrObjectionProcessFacade;

    @Operation(summary = "인사 평가 이의제기 처리 (승인/반려)", description = "팀장이 이의제기를 승인하거나 반려 처리합니다.")
    @PatchMapping("/process")
    public ApiResponse<HrObjectionProcessResponseDto> processObjection(
            @RequestBody @Valid HrObjectionProcessRequestDto request
    ) {
        HrObjectionProcessResponseDto response = hrObjectionProcessFacade.processObjection(request);
        return ApiResponse.success(response);
    }
}
