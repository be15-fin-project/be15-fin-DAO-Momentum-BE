package com.dao.momentum.evaluation.hr.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.hr.query.dto.request.HrObjectionListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.HrObjectionListResultDto;
import com.dao.momentum.evaluation.hr.query.service.HrObjectionQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hr-objections")
@RequiredArgsConstructor
@Tag(name = "인사 평가 이의제기", description = "팀장이 자신이 작성한 사원의 인사 평가 이의제기 목록 조회 API")
public class HrObjectionQueryController {

    private final HrObjectionQueryService service;

    @GetMapping("/requests")
    @Operation(
        summary = "인사 평가 이의제기 요청 조회",
        description = "팀장이 자신이 작성한 사원의 인사 평가 이의제기 목록을 상태, 회차, 날짜 필터로 조회합니다."
    )
    public ApiResponse<HrObjectionListResultDto> listObjections(
            @ParameterObject @ModelAttribute HrObjectionListRequestDto req,
            Authentication auth
    ) {
        // JWT subject(empId)를 요청 DTO에 설정
        req.setRequesterEmpId(Long.valueOf(auth.getName()));
        HrObjectionListResultDto result = service.getObjections(req);
        return ApiResponse.success(result);
    }
}
