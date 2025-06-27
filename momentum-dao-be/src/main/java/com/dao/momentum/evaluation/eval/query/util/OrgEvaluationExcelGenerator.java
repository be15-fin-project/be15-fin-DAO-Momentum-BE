package com.dao.momentum.evaluation.eval.query.util;

import com.dao.momentum.evaluation.eval.query.dto.response.OrgEvaluationExcelDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class OrgEvaluationExcelGenerator {

    private static final String[] HEADERS = {
        "회차번호", "평가자 사번", "평가자 이름", "부서", "직위", "점수", "제출일시"
    };

    public static byte[] generate(List<OrgEvaluationExcelDto> dataList) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("조직 평가 내역");

            // 헤더 스타일
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // 1. 헤더 생성
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // 2. 데이터 행
            int rowIdx = 1;
            for (OrgEvaluationExcelDto dto : dataList) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(dto.roundNo());
                row.createCell(1).setCellValue(dto.evalEmpNo());
                row.createCell(2).setCellValue(dto.evalName());
                row.createCell(3).setCellValue(dto.departmentName());
                row.createCell(4).setCellValue(dto.positionName());
                row.createCell(5).setCellValue(dto.score() != null ? dto.score() : 0);
                row.createCell(6).setCellValue(dto.submittedAt() != null ? dto.submittedAt() : "-");
            }

            // 3. 열 너비 자동 조정
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("조직 평가 엑셀 생성 실패", e);
        }
    }
}
