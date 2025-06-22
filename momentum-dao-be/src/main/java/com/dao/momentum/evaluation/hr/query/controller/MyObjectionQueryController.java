package com.dao.momentum.evaluation.hr.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.hr.query.dto.request.MyObjectionListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.MyObjectionListResultDto;
import com.dao.momentum.evaluation.hr.query.dto.response.ObjectionDetailResultDto;
import com.dao.momentum.evaluation.hr.query.dto.response.ObjectionListResultDto;
import com.dao.momentum.evaluation.hr.query.service.MyObjectionQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hr-objections")
@RequiredArgsConstructor
@Tag(name = "인사 평가 이의제기", description = "사원이 본인이 제기한 이의제기 내역 조회 API")
public class MyObjectionQueryController {

    private final MyObjectionQueryService service;

    @GetMapping("/my")
    @Operation(
            summary = "본인 이의제기 내역 조회",
            description = "로그인한 사원이 본인이 제기한 인사 평가 이의제기 내역을 페이징 조회합니다."
    )
    public ApiResponse<MyObjectionListResultDto> getMyObjections(
            @ParameterObject MyObjectionListRequestDto req,
            Authentication auth
    ) {
        Long empId = Long.valueOf(auth.getName());
        MyObjectionListResultDto result = service.getMyObjections(empId, req);
        return ApiResponse.success(result);
    }

    @GetMapping("/my/{objectionId}")
    @Operation(
            summary = "본인 이의제기 상세 조회",
            description = "로그인한 사원이 본인이 제기한 특정 이의제기 건의 상세 정보를 조회합니다."
    )
    public ApiResponse<ObjectionDetailResultDto> getMyObjectionDetail(
            @PathVariable Long objectionId,
            Authentication auth
    ) {
        Long empId = Long.valueOf(auth.getName());
        ObjectionDetailResultDto dto = service.getObjectionDetail(empId, objectionId);
        return ApiResponse.success(dto);
    }
}
