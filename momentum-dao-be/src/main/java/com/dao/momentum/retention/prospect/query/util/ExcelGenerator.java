package com.dao.momentum.retention.prospect.query.util;

import com.dao.momentum.retention.prospect.query.dto.request.RetentionSupportExcelDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ExcelGenerator {

    private static final String[] HEADERS = {
        "사번", "이름", "회차번호", "부서명", "근속점수",
        "직무 만족도", "보상 만족도", "관계 만족도", "성장 만족도", "근속연수", "워라밸 만족도"
    };

    public static byte[] generate(List<RetentionSupportExcelDto> dataList) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Retention Support");

            // 스타일 설정
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // 헤더 생성
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // 데이터 행 생성
            int rowIdx = 1;
            for (RetentionSupportExcelDto dto : dataList) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(dto.employeeNo());
                row.createCell(1).setCellValue(dto.employeeName());
                row.createCell(2).setCellValue(dto.roundNo());
                row.createCell(3).setCellValue(dto.deptName());
                row.createCell(4).setCellValue(safeDouble(dto.retentionScore()));
                row.createCell(5).setCellValue(safeDouble(dto.jobSatisfaction()));
                row.createCell(6).setCellValue(safeDouble(dto.compensationSatisfaction()));
                row.createCell(7).setCellValue(safeDouble(dto.relationSatisfaction()));
                row.createCell(8).setCellValue(safeDouble(dto.growthSatisfaction()));
                row.createCell(9).setCellValue(safeDouble(dto.tenure()));
                row.createCell(10).setCellValue(safeDouble(dto.wlbSatisfaction()));
            }

            // 열 자동 너비 조정
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("엑셀 생성 중 오류 발생", e);
        }
    }

    private static double safeDouble(Double value) {
        return value != null ? value : 0.0;
    }
}
