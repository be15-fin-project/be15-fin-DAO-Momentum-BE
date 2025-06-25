package com.dao.momentum.evaluation.eval.command.application.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalRoundCreateRequest;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalRoundUpdateRequest;
import com.dao.momentum.evaluation.eval.command.application.dto.response.EvalRoundCreateResponse;
import com.dao.momentum.evaluation.eval.command.application.dto.response.EvalRoundUpdateResponse;
import com.dao.momentum.evaluation.eval.command.application.facade.EvalRoundCommandFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/evaluations/rounds")
@Tag(name = "다면 평가 회차", description = "평가 회차 등록 API")
public class EvalRoundCommandController {

    private final EvalRoundCommandFacade evalRoundCommandFacade;

    @PostMapping
    @Operation(summary = "평가 회차 등록", description = "회차 번호와 평가 시작일, 가중치 및 등급 비율을 포함한 평가 회차를 등록합니다.")
    public ApiResponse<EvalRoundCreateResponse> createEvalRound(
            @RequestBody @Valid EvalRoundCreateRequest request) {
        EvalRoundCreateResponse response = evalRoundCommandFacade.createEvalRound(request);
        return ApiResponse.success(response);
    }

    @PatchMapping("/{roundId}")
    public ApiResponse<EvalRoundUpdateResponse> updateEvalRound(
            @PathVariable Integer roundId,
            @RequestBody EvalRoundUpdateRequest request
    ) {
        EvalRoundUpdateResponse response = evalRoundCommandFacade.updateEvalRound(roundId, request);
        return ApiResponse.success(response);
    }
}
