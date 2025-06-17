package com.dao.momentum.approve.query.controller;

import com.dao.momentum.approve.query.dto.request.ApproveListRequest;
import com.dao.momentum.approve.query.dto.request.PageRequest;
import com.dao.momentum.approve.query.dto.response.ApproveResponse;
import com.dao.momentum.approve.query.service.ApproveService;
import com.dao.momentum.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/approval")
@Tag(name = "결재 내역 조회 및 추가", description ="결재 내역 조회 및 추가 API")
@RequiredArgsConstructor
public class ApproveController {

    private final ApproveService approveService;

    // 받은 결재 문서 조회 기능
    @GetMapping("/documents/received")
    @Operation(summary = "받은 결재 문서 조회", description = "사원이 받은(승인해야 하는) 결재 문서를 조회합니다.")
    public ResponseEntity<ApiResponse<ApproveResponse>> getReceivedApprovals(
            @Validated ApproveListRequest approveListRequest,
            @AuthenticationPrincipal UserDetails userDetails,
            PageRequest pageRequest
    ) {

        Long empId = Long.parseLong(userDetails.getUsername());

        ApproveResponse approveResponse
                = approveService.getReceivedApprove(approveListRequest, empId ,pageRequest);

        return ResponseEntity.ok(ApiResponse.success(approveResponse));
    }


}
