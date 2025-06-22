package com.dao.momentum.evaluation.eval.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.eval.query.dto.request.OrgEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.PeerEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.OrgEvaluationListResultDto;
import com.dao.momentum.evaluation.eval.query.dto.response.PeerEvaluationDetailResultDto;
import com.dao.momentum.evaluation.eval.query.dto.response.PeerEvaluationListResultDto;
import com.dao.momentum.evaluation.eval.query.service.EvaluationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/evaluation/results")
@Tag(name = "평가 결과 조회", description = "사원 간 평가, 조직 평가, 자가 진단 결과를 조회하는 API")
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

    @GetMapping("/peer/{resultId}")
    @Operation(summary = "사원 간 평가 상세 조회", description = "해당 평가 결과의 요인별 점수, 사유, 종합 점수를 상세히 조회합니다.")
    public ApiResponse<PeerEvaluationDetailResultDto> getPeerEvaluationDetail(
            @PathVariable Long resultId
    ) {
        PeerEvaluationDetailResultDto detail = evaluationQueryService.getPeerEvaluationDetail(resultId);
        return ApiResponse.success(detail);
    }

    @GetMapping("/org")
    @Operation(summary = "조직 평가 내역 조회", description = "조직 만족도 계열 평가 결과 목록을 조회합니다.")
    public ApiResponse<OrgEvaluationListResultDto> getOrgEvaluations(
            @ModelAttribute OrgEvaluationListRequestDto requestDto
    ) {
        OrgEvaluationListResultDto result = evaluationQueryService.getOrgEvaluations(requestDto);
        return ApiResponse.success(result);
    }
}
