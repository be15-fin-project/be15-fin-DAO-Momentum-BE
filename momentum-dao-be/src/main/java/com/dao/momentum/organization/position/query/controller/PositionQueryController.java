package com.dao.momentum.organization.position.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.organization.position.query.dto.response.PositionsResponse;
import com.dao.momentum.organization.position.query.service.PositionQueryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/position")
@RequiredArgsConstructor
public class PositionQueryController {
    private final PositionQueryService positionService;

    @Operation(summary = "직위 조회", description = "관리자는 회사의 직위를 조회할 수 있다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PositionsResponse>> getPositions(){
        PositionsResponse positionsResponse = positionService.getPositions();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(positionsResponse));
    }
}
