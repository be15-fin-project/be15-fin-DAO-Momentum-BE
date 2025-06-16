package com.dao.momentum.organization.position.command.application.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.organization.position.command.application.dto.request.PositionCreateRequest;
import com.dao.momentum.organization.position.command.application.dto.response.PositionCreateResponse;
import com.dao.momentum.organization.position.command.application.service.PositionCommandService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/position")
public class PositionCommandController {
    private final PositionCommandService positionService;

    @Operation(summary = "직위 생성", description = "관리자는 회사의 직위를 생성할 수 있다.")
    @PostMapping
    public ResponseEntity<ApiResponse<PositionCreateResponse>> createPositions(@RequestBody PositionCreateRequest request){
        PositionCreateResponse positionsResponse = positionService.createPositions(request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(positionsResponse));
    }

}
