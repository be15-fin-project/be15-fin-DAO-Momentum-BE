package com.dao.momentum.evaluation.hr.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.hr.query.dto.request.MyHrEvaluationListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.HrEvaluationCriteriaDto;
import com.dao.momentum.evaluation.hr.query.dto.response.HrEvaluationDetailResultDto;
import com.dao.momentum.evaluation.hr.query.dto.response.HrEvaluationListResultDto;
import com.dao.momentum.evaluation.hr.query.service.EvaluationHrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/evaluations/hr")
@RequiredArgsConstructor
@Tag(name = "인사 평가 조회", description = "사원이 본인의 인사 평가 내역 및 상세 정보를 조회하는 API")
public class EvaluationHrController {

    private final EvaluationHrService evaluationHrService;

    @GetMapping
    @Operation(summary = "사원 본인 인사 평가 내역 조회", description = "본인의 인사 평가 내역을 조건(기간 등)으로 조회한다.")
    public ApiResponse<HrEvaluationListResultDto> getMyHrEvaluations(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute MyHrEvaluationListRequestDto req
    ) {
        Long empId = Long.parseLong(userDetails.getUsername());
        HrEvaluationListResultDto result = evaluationHrService.getHrEvaluations(empId, req);
        return ApiResponse.success(result);
    }

    @GetMapping("/{resultId}")
    @Operation(summary = "사원 본인 인사 평가 상세 조회", description = "선택한 평가 결과에 대한 상세 항목을 조회한다. 종합 등급, 요인별 점수, 가중치, 등급 비율 등을 확인할 수 있다.")
    public ApiResponse<HrEvaluationDetailResultDto> getHrEvaluationDetail(
            @PathVariable Long resultId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long empId = Long.parseLong(userDetails.getUsername());
        HrEvaluationDetailResultDto result = evaluationHrService.getHrEvaluationDetail(empId, resultId);
        return ApiResponse.success(result);
    }

    @GetMapping("/{roundNo}/criteria")
    @Operation(summary = "인사 평가 기준 조회")
    public ApiResponse<HrEvaluationCriteriaDto> getEvaluationCriteria(
            @PathVariable Integer roundNo
    ) {
        return ApiResponse.success(evaluationHrService.getEvaluationCriteria(roundNo));
    }

}
