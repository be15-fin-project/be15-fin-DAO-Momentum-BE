package com.dao.momentum.evaluation.eval.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.eval.query.dto.request.PeerEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.PeerEvaluationListResultDto;
import com.dao.momentum.evaluation.eval.query.service.EvaluationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/evaluation/results")
@Tag(name = "사원 간 평가 결과 조회", description = "동료/상향/하향/인사 평가 결과를 조회하는 API")
public class EvaluationQueryController {

    private final EvaluationQueryService evaluationQueryService;

    @GetMapping("/peer")
    @Operation(summary = "사원 간 평가 내역 조회", description = "동료, 상향, 하향, 인사 평가 결과 목록을 조회합니다.")
    public ApiResponse<PeerEvaluationListResultDto> getPeerEvaluations(
            @ModelAttribute PeerEvaluationListRequestDto requestDto
    ) {
        PeerEvaluationListResultDto result = evaluationQueryService.getPeerEvaluations(requestDto);
        return ApiResponse.success(result);
    }
}
