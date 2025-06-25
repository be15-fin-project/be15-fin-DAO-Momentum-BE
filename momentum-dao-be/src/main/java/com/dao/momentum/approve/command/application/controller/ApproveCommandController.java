package com.dao.momentum.approve.command.application.controller;

import com.dao.momentum.approve.command.application.dto.request.ApproveRequest;
import com.dao.momentum.approve.command.application.dto.response.ReceiptOcrResultResponse;
import com.dao.momentum.approve.command.application.service.ApproveCommandService;
import com.dao.momentum.approve.command.application.service.OcrService;
import com.dao.momentum.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/approval")
@Tag(name = "결재 작성 하기", description = "결재 작성 API")
public class ApproveCommandController {

    private final OcrService ocrService;
    private final ApproveCommandService approveCommandService;

    @PostMapping("/ocr/receipt")
    @Operation(summary = "영수증 내용 추출하기", description = "ocr api를 이용해 영수증 내용을 추출합니다.")
    public ResponseEntity<ApiResponse<ReceiptOcrResultResponse>> extractReceiptText (
            @RequestParam("file") MultipartFile file
    ) {
        ReceiptOcrResultResponse result = ocrService.extractReceiptData(file);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/documents")
    @Operation(summary = "결재 작성 하기", description = "사원이 결재 서류를 작성합니다.")
    public ResponseEntity<ApiResponse<Void>> createApprovalDocument (
            @RequestBody @Valid ApproveRequest approveRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        Long empId = Long.parseLong(userDetails.getUsername());

        approveCommandService.createApproval(approveRequest, empId);

        return ResponseEntity.ok(ApiResponse.success(null));
    }


}
