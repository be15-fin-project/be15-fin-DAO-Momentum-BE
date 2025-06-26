package com.dao.momentum.retention.prospect.query.controller;

import com.dao.momentum.retention.prospect.query.service.RetentionSupportExcelDownloadService;
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
@RequestMapping("/retention-supports")
@Tag(name = "Retention Support", description = "근속 지원 조회 및 엑셀 다운로드 API")
public class RetentionSupportExcelDownloadController {

    private final RetentionSupportExcelDownloadService excelDownloadService;

    @GetMapping("/excel")
    @Operation(summary = "근속 지원 엑셀 다운로드", description = "회차, 부서, 안정성 유형으로 필터링된 근속 지원 데이터를 엑셀로 다운로드합니다.")
    public ResponseEntity<byte[]> downloadExcel(
            @Parameter(description = "회차 ID", example = "3")
            @RequestParam(required = false) Long roundId,

            @Parameter(description = "부서 ID", example = "101")
            @RequestParam(required = false) Long deptId,

            @Parameter(description = "안정성 유형", example = "STABLE")
            @RequestParam(required = false) String stabilityType
    ) {
        byte[] file = excelDownloadService.downloadExcel(roundId, deptId, stabilityType);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=retention-support.xlsx")
                .body(file);
    }
}
