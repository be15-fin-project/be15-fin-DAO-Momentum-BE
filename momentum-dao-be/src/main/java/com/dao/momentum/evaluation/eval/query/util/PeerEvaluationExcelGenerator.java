package com.dao.momentum.evaluation.eval.query.util;

import com.dao.momentum.evaluation.eval.query.dto.response.various.PeerEvaluationExcelDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class PeerEvaluationExcelGenerator {

    private static final String[] HEADERS = {
        "회차번호", "평가 종류", "피평가자 사번", "피평가자 이름", "부서", "직위",
        "평가자 사번", "평가자 이름", "점수", "제출일시"
    };

    public static byte[] generate(List<PeerEvaluationExcelDto> dataList) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Peer 평가 내역");

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
            for (PeerEvaluationExcelDto dto : dataList) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(dto.roundNo());
                row.createCell(1).setCellValue(dto.formName());
                row.createCell(2).setCellValue(dto.targetEmpNo());
                row.createCell(3).setCellValue(dto.targetName());
                row.createCell(4).setCellValue(dto.departmentName());
                row.createCell(5).setCellValue(dto.positionName());
                row.createCell(6).setCellValue(dto.evaluatorEmpNo());
                row.createCell(7).setCellValue(dto.evaluatorName());
                row.createCell(8).setCellValue(dto.score() != null ? dto.score() : 0);
                row.createCell(9).setCellValue(dto.submittedAt() != null ? dto.submittedAt() : "-");
            }

            // 3. 열 너비 자동 조정
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Peer 평가 엑셀 생성 실패", e);
        }
    }
}
