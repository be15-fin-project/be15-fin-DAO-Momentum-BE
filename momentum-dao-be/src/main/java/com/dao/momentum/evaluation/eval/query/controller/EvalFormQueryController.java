package com.dao.momentum.evaluation.eval.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.eval.query.dto.response.EvalFormDetailResultDto;
import com.dao.momentum.evaluation.eval.query.service.EvalFormQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "다면 평가 제출", description = "사원용 평가 제출 상세 조회")
@RestController
@RequiredArgsConstructor
@RequestMapping("/eval-forms")
public class EvalFormQueryController {

    private final EvalFormQueryService evalFormQueryService;

    @Operation(summary = "평가 양식 상세 조회", description = "선택한 평가 양식의 요인 및 문항 목록을 조회한다.")
    @GetMapping("/{formId}")
    public ApiResponse<EvalFormDetailResultDto> getFormDetail(
            @Parameter(description = "평가 양식 ID", example = "1")
            @PathVariable("formId") Integer formId,

            @Parameter(description = "평가 회차 ID", example = "3")
            @RequestParam(value = "roundId", required = false) Integer roundId
    ) {
        EvalFormDetailResultDto result = evalFormQueryService.getFormDetail(formId, roundId);
        return ApiResponse.success(result);
    }
}
