package com.dao.momentum.approve.query.controller;

import com.dao.momentum.approve.query.dto.request.ApproveListRequest;
import com.dao.momentum.approve.query.dto.request.PageRequest;
import com.dao.momentum.approve.query.dto.response.ApproveResponse;
import com.dao.momentum.approve.query.service.AdminApproveService;
import com.dao.momentum.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminApproveController {

    private final AdminApproveService adminApproveService;

    /* 전체 결재 목록 조회하기 */
    @GetMapping("/approval/documents")
    @PreAuthorize("hasAuthority('MASTER')")
    public ResponseEntity<ApiResponse<ApproveResponse>> getApproveList(
            @Validated ApproveListRequest approveListRequest,
            PageRequest pageRequest
    ) {

        ApproveResponse approveResponse
                = adminApproveService.getApproveList(approveListRequest, pageRequest);

        return ResponseEntity.ok(ApiResponse.success(approveResponse));
    }

}
