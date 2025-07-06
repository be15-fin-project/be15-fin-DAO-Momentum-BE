package com.dao.momentum.evaluation.eval.query.controller;

import com.dao.momentum.evaluation.eval.query.dto.request.various.OrgEvaluationExcelRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.various.PeerEvaluationExcelRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.various.SelfEvaluationExcelRequestDto;
import com.dao.momentum.evaluation.eval.query.service.ExcelEvaluationDownloadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/evaluations/excel")
@Tag(name = "평가 엑셀 다운로드", description = "다면 평가(Peer, Org, Self) 엑셀 다운로드 API")
public class ExcelEvaluationDownloadController {

    private final ExcelEvaluationDownloadService excelDownloadService;

    @GetMapping("/peer")
    @Operation(summary = "사원간 평가 엑셀 다운로드", description = "회차, 피평가자, 평가자, 부서, 직위 등으로 필터링된 Peer 평가 결과를 엑셀로 다운로드합니다.")
    public ResponseEntity<byte[]> downloadPeerEvaluationExcel(
            @Parameter(description = "필터 조건 (회차, 사번 등)")
            @ModelAttribute PeerEvaluationExcelRequestDto request
    ) {
        byte[] file = excelDownloadService.downloadPeerEvaluationExcel(request);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=peer-evaluation.xlsx")
                .body(file);
    }

    @GetMapping("/org")
    @Operation(summary = "조직 평가 엑셀 다운로드", description = "회차, 피평가자, 평가자, 부서, 직위 등으로 필터링된 조직 평가 결과를 엑셀로 다운로드합니다.")
    public ResponseEntity<byte[]> downloadOrgEvaluationExcel(
            @Parameter(description = "필터 조건 (회차, 사번 등)")
            @ModelAttribute OrgEvaluationExcelRequestDto request
    ) {
        byte[] file = excelDownloadService.downloadOrgEvaluationExcel(request);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=org-evaluation.xlsx")
                .body(file);
    }

    @GetMapping("/self")
    @Operation(
            summary = "자가 진단 평가 엑셀 다운로드",
            description = "회차 ID, 사번, 부서 ID, 직위 ID, 제출 여부로 필터링된 자가 진단 평가 결과를 엑셀로 다운로드합니다."
    )
    public ResponseEntity<byte[]> downloadSelfEvaluationExcel(
            @Parameter(description = "필터 조건 (회차, 사번 등)")
            @ModelAttribute SelfEvaluationExcelRequestDto request
    ) {
        byte[] file = excelDownloadService.downloadSelfEvaluationExcel(request);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=self-evaluation.xlsx")
                .body(file);
    }
}
