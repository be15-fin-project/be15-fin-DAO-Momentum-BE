package com.dao.momentum.retention.command.application.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.retention.command.application.dto.request.*;
import com.dao.momentum.retention.command.application.dto.response.RetentionContactDeleteResponse;
import com.dao.momentum.retention.command.application.dto.response.RetentionContactResponse;
import com.dao.momentum.retention.command.application.dto.response.RetentionContactResponseUpdateResponse;
import com.dao.momentum.retention.command.application.service.RetentionContactCommandService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/retention-contacts")
public class RetentionContactCommandController {

    private final RetentionContactCommandService contactCommandService;

    @PostMapping
    public ApiResponse<RetentionContactResponse> createContact(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody CreateRetentionContactRequest request
    ) {
        Long empId = Long.parseLong(user.getUsername());
        // Request → DTO 변환
        RetentionContactCreateDto dto = RetentionContactCreateDto.builder()
                .targetId(request.targetId())
                .managerId(request.managerId())
                .writerId(empId)
                .reason(request.reason())
                .build();

        // 생성 요청
        RetentionContactResponse response = contactCommandService.createContact(dto);
        return ApiResponse.success(response);
    }

    @DeleteMapping("/{retentionId}")
    public ApiResponse<RetentionContactDeleteResponse> deleteContact(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable @NotNull Long retentionId
    ) {
        Long empId = Long.parseLong(user.getUsername());
        RetentionContactDeleteDto dto = RetentionContactDeleteDto.builder()
                .retentionId(retentionId)
                .loginEmpId(empId)
                .build();

        RetentionContactDeleteResponse response = contactCommandService.deleteContact(dto);
        return ApiResponse.success(response);
    }

    @PostMapping("/{retentionId}/response")
    public ApiResponse<RetentionContactResponseUpdateResponse> reportResponse(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long retentionId,
            @Valid @RequestBody RetentionContactResponseUpdateRequest request
    ) {
        Long empId = Long.parseLong(user.getUsername());
        RetentionContactResponseUpdateDto dto = RetentionContactResponseUpdateDto.builder()
                .retentionId(retentionId)
                .loginEmpId(empId)
                .response(request.response())
                .build();

        RetentionContactResponseUpdateResponse response = contactCommandService.reportResponse(dto);
        return ApiResponse.success(response);
    }
}
