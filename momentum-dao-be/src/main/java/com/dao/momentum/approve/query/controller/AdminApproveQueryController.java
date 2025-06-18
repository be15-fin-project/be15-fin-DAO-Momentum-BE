package com.dao.momentum.approve.query.controller;

import com.dao.momentum.approve.query.dto.request.ApproveListRequest;
import com.dao.momentum.approve.query.dto.request.PageRequest;
import com.dao.momentum.approve.query.dto.response.ApproveResponse;
import com.dao.momentum.approve.query.service.AdminApproveQueryService;
import com.dao.momentum.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Tag(name = "전체 결재 내역 조회", description = "전체 결재 내역 조회 API")
@RequiredArgsConstructor
public class AdminApproveQueryController {

    private final AdminApproveQueryService adminApproveQueryService;

    /* 전체 결재 목록 조회하기 */
    @GetMapping("/approval/documents")
    @PreAuthorize("hasAuthority('MASTER')")
    @Operation(summary = "전체 결재 내역 조회", description = "마스터 관리자가 전체 결재 내역을 조회합니다.")
    public ResponseEntity<ApiResponse<ApproveResponse>> getApproveList(
            @Validated ApproveListRequest approveListRequest,
            PageRequest pageRequest
    ) {

        ApproveResponse approveResponse
                = adminApproveQueryService.getApproveList(approveListRequest, pageRequest);

        return ResponseEntity.ok(ApiResponse.success(approveResponse));
    }

}
