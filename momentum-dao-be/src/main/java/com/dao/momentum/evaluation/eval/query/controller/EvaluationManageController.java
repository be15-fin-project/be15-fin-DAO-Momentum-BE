package com.dao.momentum.evaluation.eval.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationFormListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationRoundListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationFormResponseDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationRoundListResultDto;
import com.dao.momentum.evaluation.eval.query.service.EvaluationManageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/evaluations")
public class EvaluationManageController {

    private final EvaluationManageService evaluationManageService;

    @GetMapping("/rounds")
    @Operation(summary = "다면 평가 회차 목록 조회", description = "관리자 또는 인사팀이 다면 평가 회차 목록을 조건별로 조회합니다.")
    public ApiResponse<EvaluationRoundListResultDto> getEvaluationRounds(
            @ModelAttribute EvaluationRoundListRequestDto request
    ) {
        EvaluationRoundListResultDto result = evaluationManageService.getEvaluationRounds(request);
        return ApiResponse.success(result);
    }

    @GetMapping("/forms")
    @Operation(summary = "평가 양식 목록 조회", description = "유형에 따라 평가 양식 리스트를 조회합니다.")
    public ApiResponse<List<EvaluationFormResponseDto>> getEvaluationForms(
            @ModelAttribute EvaluationFormListRequestDto request
    ) {
        List<EvaluationFormResponseDto> result = evaluationManageService.getEvaluationForms(request);
        return ApiResponse.success(result);
    }
}
