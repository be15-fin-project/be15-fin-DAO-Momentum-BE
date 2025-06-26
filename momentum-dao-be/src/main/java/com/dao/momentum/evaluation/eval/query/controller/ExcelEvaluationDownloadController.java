package com.dao.momentum.evaluation.eval.query.controller;

import com.dao.momentum.evaluation.eval.query.dto.request.OrgEvaluationExcelRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.PeerEvaluationExcelRequestDto;
import com.dao.momentum.evaluation.eval.query.service.ExcelEvaluationDownloadService;
import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<byte[]> downloadPeerEvaluationExcel(@ModelAttribute PeerEvaluationExcelRequestDto request) {
        byte[] file = excelDownloadService.downloadPeerEvaluationExcel(request);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=peer-evaluation.xlsx")
                .body(file);
    }

    @GetMapping("/org")
    @Operation(summary = "조직 평가 엑셀 다운로드", description = "회차, 피평가자, 평가자, 부서, 직위 등으로 필터링된 조직 평가 결과를 엑셀로 다운로드합니다.")
    public ResponseEntity<byte[]> downloadOrgEvaluationExcel(@ModelAttribute OrgEvaluationExcelRequestDto request) {
        byte[] file = excelDownloadService.downloadOrgEvaluationExcel(request);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=org-evaluation.xlsx")
                .body(file);
    }
}
