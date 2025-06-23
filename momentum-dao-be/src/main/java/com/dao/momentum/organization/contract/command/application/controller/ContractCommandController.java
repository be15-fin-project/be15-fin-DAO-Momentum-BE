package com.dao.momentum.organization.contract.command.application.controller;

import com.dao.momentum.organization.contract.command.application.dto.request.ContractCreateRequest;
import com.dao.momentum.organization.contract.command.application.dto.response.ContractCreateResponse;
import com.dao.momentum.organization.contract.command.application.service.ContractCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/contracts")
@RequiredArgsConstructor
@Tag(name = "계약서 관리", description = "계약서 등록, 수정, 삭제 API")
public class ContractCommandController {

    private final ContractCommandService contractCommandService;

    @Operation(summary = "계약서 등록", description = "관리자는 계약서를 등록할 수 있다.")
    @PutMapping
    public ResponseEntity<ContractCreateResponse> createContract(
            @RequestBody @Valid ContractCreateRequest contractCreateRequest,
            @AuthenticationPrincipal UserDetails userDetails                                                     ) {
        ContractCreateResponse response = contractCommandService.createContract(contractCreateRequest, userDetails);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
