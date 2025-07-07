package com.dao.momentum.evaluation.hr.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.hr.query.dto.request.HrObjectionListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.HrObjectionListResultDto;
import com.dao.momentum.evaluation.hr.query.dto.response.ObjectionDetailResultDto;
import com.dao.momentum.evaluation.hr.query.service.HrObjectionQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hr-objections")
@RequiredArgsConstructor
@Tag(name = "인사 평가 이의제기 요청", description = "팀장이 자신이 작성한 사원의 인사 평가 이의제기 목록 조회 API")
public class HrObjectionQueryController {

    private final HrObjectionQueryService service;

    @GetMapping("/requests")
    @Operation(
        summary = "인사 평가 이의제기 요청 조회",
        description = "팀장이 자신이 작성한 사원의 인사 평가 이의제기 목록을 상태, 회차, 날짜 필터로 조회합니다."
    )
    public ApiResponse<HrObjectionListResultDto> listObjections(
            @AuthenticationPrincipal UserDetails userDetails,
            @ParameterObject @ModelAttribute HrObjectionListRequestDto req
    ) {
        Long empId = Long.parseLong(userDetails.getUsername());
        HrObjectionListResultDto result = service.getObjections(empId, req);
        return ApiResponse.success(result);
    }

    @GetMapping("/requests/{objectionId}")
    @Operation(
            summary = "이의제기 요청 상세 조회",
            description = "로그인한 사원이 본인에게 제기된 특정 이의제기 건의 상세 정보를 조회합니다."
    )
    public ApiResponse<ObjectionDetailResultDto> getMyObjectionDetail(
            @PathVariable Long objectionId
    ) {
        ObjectionDetailResultDto dto = service.getObjectionDetail(objectionId);
        return ApiResponse.success(dto);
    }
}
