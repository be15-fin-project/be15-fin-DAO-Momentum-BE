package com.dao.momentum.evaluation.kpi.query.controller;

import com.dao.momentum.evaluation.kpi.query.dto.request.KpiExelRequestDto;
import com.dao.momentum.evaluation.kpi.query.service.KpiExcelDownloadService;
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
@RequestMapping("/kpi")
@Tag(name = "KPI 엑셀 다운로드", description = "KPI 엑셀 다운로드 API")
public class KpiExcelDownloadController {

    private final KpiExcelDownloadService kpiExcelDownloadService;

    @GetMapping("/excel")
    @Operation(
        summary = "KPI 엑셀 다운로드",
        description = "사번, 부서, 직위, 상태, 작성일 조건으로 필터링된 KPI 데이터를 엑셀로 다운로드합니다."
    )
    public ResponseEntity<byte[]> downloadExcel(
            @Parameter(description = "필터링 파라미터") @ModelAttribute KpiExelRequestDto requestDto
    ) {
        byte[] excelFile = kpiExcelDownloadService.downloadKpisAsExcel(requestDto);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=kpi-list.xlsx")
                .body(excelFile);
    }
}
