package com.dao.momentum.evaluation.eval.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationTaskRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationTaskListResultDto;
import com.dao.momentum.evaluation.eval.query.service.EvaluationTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/evaluation")
@RequiredArgsConstructor
@Tag(name = "평가 태스크", description = "내가 작성해야 할 평가 목록 조회 API")
public class EvaluationTaskController {

    private final EvaluationTaskService taskService;

    @GetMapping("/tasks")
    @Operation(
        summary = "내 평가 태스크 조회",
        description = "인증된 사원의 DMP ID를 통해 평가(SELF, ORG, PEER) 태스크 목록을 조회합니다."
    )
    public ResponseEntity<ApiResponse<EvaluationTaskListResultDto>> getMyTasks(
        @AuthenticationPrincipal UserDetails user,
        @ModelAttribute EvaluationTaskRequestDto req
    ) {
        // UserDetails.getUsername()에 DMP ID가 담겨 있다고 가정
        Long empId = Long.parseLong(user.getUsername());

        // 서비스 호출
        EvaluationTaskListResultDto result = taskService.getTasks(empId, req);

        return ResponseEntity.ok(ApiResponse.success(result));
    }
}