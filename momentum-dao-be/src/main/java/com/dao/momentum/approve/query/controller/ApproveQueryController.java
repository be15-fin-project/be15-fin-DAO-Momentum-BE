package com.dao.momentum.approve.query.controller;

import com.dao.momentum.approve.query.dto.EmployeeLeaderDto;
import com.dao.momentum.approve.query.dto.request.ApproveListRequest;
import com.dao.momentum.approve.query.dto.request.DraftApproveListRequest;
import com.dao.momentum.approve.query.dto.request.PageRequest;
import com.dao.momentum.approve.query.dto.response.ApproveDetailResponse;
import com.dao.momentum.approve.query.dto.response.ApproveResponse;
import com.dao.momentum.approve.query.dto.response.DraftApproveResponse;
import com.dao.momentum.approve.query.service.ApproveQueryService;
import com.dao.momentum.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/approval")
@Tag(name = "결재 내역 조회 및 추가", description ="결재 내역 조회 및 추가 API")
@RequiredArgsConstructor
public class ApproveQueryController {

    private final ApproveQueryService approveQueryService;

    /* 받은 결재 문서 조회 기능 */
    @GetMapping("/documents/received")
    @Operation(summary = "받은 결재 문서 조회", description = "사원이 받은(승인해야 하는) 결재 문서를 조회합니다.")
    public ResponseEntity<ApiResponse<ApproveResponse>> getReceivedApprovals(
            @Validated ApproveListRequest approveListRequest,
            @AuthenticationPrincipal UserDetails userDetails,
            PageRequest pageRequest
    ) {

        Long empId = Long.parseLong(userDetails.getUsername());

        ApproveResponse approveResponse
                = approveQueryService.getReceivedApprove(approveListRequest, empId ,pageRequest);

        return ResponseEntity.ok(ApiResponse.success(approveResponse));
    }

    /* 보낸 결재 문서 조회 기능 */
    @GetMapping("/documents/draft")
    @Operation(summary = "보낸 결재 문서 조회", description = "사원 본인이 보낸 결재 문서를 조회합니다.")
    public ResponseEntity<ApiResponse<DraftApproveResponse>> getDraftApprovals(
            @Validated DraftApproveListRequest draftApproveListRequest,
            @AuthenticationPrincipal UserDetails userDetails,
            PageRequest pageRequest
    ) {

        Long empId = Long.parseLong(userDetails.getUsername());

        DraftApproveResponse draftApproveResponse
                = approveQueryService.getDraftApprove(draftApproveListRequest, empId ,pageRequest);

        return ResponseEntity.ok(ApiResponse.success(draftApproveResponse));
    }

    /* 문서 상제 조회 기능 */
    @GetMapping("/documents/{documentId}")
    @Operation(summary = "결재 문서 상세 조회", description = "결재 문서를 상세 조회합니다.")
    public ResponseEntity<ApiResponse<ApproveDetailResponse>> getDraftApprovals(
            @PathVariable Long documentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long empId = Long.parseLong(userDetails.getUsername());

        ApproveDetailResponse approveDetailResponse
                = approveQueryService.getApproveDetail(documentId, empId);

        return ResponseEntity.ok(ApiResponse.success(approveDetailResponse));
    }

    /* 사원의 팀장 조회 기능 */
    @GetMapping("/leader")
    @Operation(
            summary = "사원의 팀장 조회",
            description = "사원의 팀장을 조회합니다. 만약 팀장 권한을 가지고 있는 사원이라면 상위 부서의 팀장을 조회합니다."
    )
    public ResponseEntity<ApiResponse<EmployeeLeaderDto>> getLeader(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long empId = Long.parseLong(userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.success(approveQueryService.getEmployeeLeader(empId)));
    }

}
