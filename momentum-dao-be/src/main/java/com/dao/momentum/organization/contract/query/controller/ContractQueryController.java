package com.dao.momentum.organization.contract.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.organization.contract.query.dto.request.AdminContractSearchRequest;
import com.dao.momentum.organization.contract.query.dto.request.ContractSearchRequest;
import com.dao.momentum.organization.contract.query.dto.response.AdminContractListResponse;
import com.dao.momentum.organization.contract.query.dto.response.ContractListResponse;
import com.dao.momentum.organization.contract.query.service.ContractQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor
@Tag(name = "계약 조회", description = "계약서 조회 API")
public class ContractQueryController {
    private final ContractQueryService contractQueryService;

    @GetMapping
    @Operation(summary = "계약서 전체 목록 조회", description = "관리자가 회사의 계약서 전체 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<AdminContractListResponse>> getContracts(
            @ModelAttribute AdminContractSearchRequest adminContractSearchRequest
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        contractQueryService.getContracts(adminContractSearchRequest)
                )
        );
    }

    @GetMapping("/me")
    @Operation(summary = "사원 계약서 목록 조회", description = "사원이 본인 계약서 전체 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<ContractListResponse>> getMyContracts(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute ContractSearchRequest contractSearchRequest
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        contractQueryService.getMyContracts(userDetails, contractSearchRequest)
                )
        );
    }

}
