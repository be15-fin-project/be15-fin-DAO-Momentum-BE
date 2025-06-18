package com.dao.momentum.organization.company.query.controller;

import com.dao.momentum.organization.company.query.dto.response.CompanyGetResponse;
import com.dao.momentum.organization.company.query.service.CompanyQueryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyQueryController {
    private final CompanyQueryService companyQueryService;

    @Operation(summary = "회사 정보 조회", description = "모든 사원은 회사의 정보를 조회할 수 있다.")
    @GetMapping
    public ResponseEntity<CompanyGetResponse> getCompany(){
        CompanyGetResponse response = companyQueryService.getCompany();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
