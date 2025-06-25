package com.dao.momentum.evaluation.eval.command.application.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalSubmitRequest;
import com.dao.momentum.evaluation.eval.command.application.dto.response.EvalSubmitResponse;
import com.dao.momentum.evaluation.eval.command.application.service.EvalSubmitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/evaluations")
@RequiredArgsConstructor
@Tag(name = "다면 평가 제출", description = "사원 다면 평가 제출 API")
public class EvalSubmitController {

    private final EvalSubmitService evalSubmitService;

    @Operation(summary = "다면 평가 제출", description = "사원이 다면 평가를 제출합니다.")
    @PostMapping("/submit")
    public ApiResponse<EvalSubmitResponse> submitEvaluation(
            @AuthenticationPrincipal UserDetails user,
            @RequestBody @Valid EvalSubmitRequest request
    ) {
        Long empId = Long.parseLong(user.getUsername());
        EvalSubmitResponse response = evalSubmitService.submitEvaluation(empId, request);
        return ApiResponse.success(response);
    }
}
