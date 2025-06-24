package com.dao.momentum.retention.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.retention.query.dto.request.RetentionRoundSearchRequestDto;
import com.dao.momentum.retention.query.dto.response.RetentionRoundListResultDto;
import com.dao.momentum.retention.query.service.RetentionRoundQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/retention/rounds")
@RequiredArgsConstructor
@Tag(name = "근속 회차 관리", description = "근속 전망 회차 정보 API")
public class RetentionRoundQueryController {

    private final RetentionRoundQueryService retentionRoundQueryService;

    @GetMapping
    @Operation(summary = "근속 전망 회차 목록 조회")
    public ApiResponse<RetentionRoundListResultDto> getRetentionRounds(
            @ModelAttribute RetentionRoundSearchRequestDto req
    ) {
        RetentionRoundListResultDto result = retentionRoundQueryService.getRetentionRounds(req);
        return ApiResponse.success(result);
    }
}
