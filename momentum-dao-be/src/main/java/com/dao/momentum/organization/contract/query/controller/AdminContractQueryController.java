package com.dao.momentum.organization.contract.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.organization.contract.query.dto.request.ContractSearchRequest;
import com.dao.momentum.organization.contract.query.dto.response.ContractListResponse;
import com.dao.momentum.organization.contract.query.service.AdminContractQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor
@Tag(name = "계약 조회", description = "관리자 계약 조회 API")
public class AdminContractQueryController {
    private final AdminContractQueryService adminContractQueryService;

    @GetMapping
    @Operation(summary = "계약서 전체 목록 조회", description = "관리자가 회사의 계약서 전체 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<ContractListResponse>> getContracts(
            @ModelAttribute ContractSearchRequest contractSearchRequest
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        adminContractQueryService.getContracts(contractSearchRequest)
                )
        );
    }

}
