package com.dao.momentum.organization.company.command.application.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.company.command.application.dto.request.CompanyUpdateRequest;
import com.dao.momentum.organization.company.command.application.dto.response.CompanyUpdateResponse;
import com.dao.momentum.organization.company.command.application.service.CompanyCommandService;
import com.dao.momentum.organization.company.exception.CompanyException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyCommandController {
    private final CompanyCommandService companyCommandService;

    @Operation(summary = "회사 정보 수정", description = "관리자 권한을 가진 사원만이 회사 정보를 수정할 수 있다.")
    @PutMapping
    public ResponseEntity<CompanyUpdateResponse> updateCompany(@RequestBody @Valid CompanyUpdateRequest companyUpdateRequest){
        CompanyUpdateResponse response = companyCommandService.updateCompany(companyUpdateRequest);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ExceptionHandler(CompanyException.class)
    public ResponseEntity<ApiResponse<Void>> handleCompanyException(CompanyException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(errorCode.getCode(), errorCode.getMessage()));
    }
}
