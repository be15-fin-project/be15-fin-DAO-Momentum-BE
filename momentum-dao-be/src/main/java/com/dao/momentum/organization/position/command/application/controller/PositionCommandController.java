package com.dao.momentum.organization.position.command.application.controller;

import com.dao.momentum.announcement.exception.AnnouncementAccessDeniedException;
import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.position.command.application.dto.request.PositionCreateRequest;
import com.dao.momentum.organization.position.command.application.dto.request.PositionUpdateRequest;
import com.dao.momentum.organization.position.command.application.dto.response.PositionCreateResponse;
import com.dao.momentum.organization.position.command.application.dto.response.PositionDeleteResponse;
import com.dao.momentum.organization.position.command.application.dto.response.PositionUpdateResponse;
import com.dao.momentum.organization.position.command.application.service.PositionCommandService;
import com.dao.momentum.organization.position.exception.PositionException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/position")
public class PositionCommandController {
    private final PositionCommandService positionService;

    @Operation(summary = "직위 생성", description = "관리자는 회사의 직위를 생성할 수 있다.")
    @PostMapping
    public ResponseEntity<ApiResponse<PositionCreateResponse>> createPosition(@RequestBody PositionCreateRequest request){
        PositionCreateResponse positionsResponse = positionService.createPositions(request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(positionsResponse));
    }

    @Operation(summary = "직위 수정", description = "관리자는 회사의 직위를 수정할 수 있다.")
    @PutMapping
    public ResponseEntity<ApiResponse<PositionUpdateResponse>> updatePosition(@RequestBody PositionUpdateRequest request){
        PositionUpdateResponse response = positionService.updatePosition(request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @Operation(summary = "직위 삭제", description = "관리자는 회사의 직위를 삭제할 수 있다.")
    @DeleteMapping("/{positionId}")
    public ResponseEntity<ApiResponse<PositionDeleteResponse>> deletePosition(@PathVariable Integer positionId){
        PositionDeleteResponse response = positionService.deletePosition(positionId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @ExceptionHandler(PositionException.class)
    public ResponseEntity<ApiResponse<Void>> handlePositionException(PositionException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(errorCode.getCode(), errorCode.getMessage()));
    }
}
