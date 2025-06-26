package com.dao.momentum.evaluation.kpi.query.util;

import com.dao.momentum.evaluation.kpi.query.dto.response.KpiExcelDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class KpiExcelGenerator {

    private static final String[] HEADERS = {
        "사번", "이름", "부서명", "직위명", "KPI 목표", "목표값",
        "진행률(%)", "상태", "작성일", "마감일"
    };

    public static byte[] generate(List<KpiExcelDto> dataList) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("KPI 목록");

            // 스타일: 헤더 bold
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // 1. 헤더 행 생성
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // 2. 데이터 행 생성
            int rowIdx = 1;
            for (KpiExcelDto dto : dataList) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(dto.employeeNo());
                row.createCell(1).setCellValue(dto.employeeName());
                row.createCell(2).setCellValue(dto.departmentName());
                row.createCell(3).setCellValue(dto.positionName());
                row.createCell(4).setCellValue(dto.goal());
                row.createCell(5).setCellValue(dto.goalValue() != null ? dto.goalValue() : 0);
                row.createCell(6).setCellValue(dto.kpiProgress() != null ? dto.kpiProgress() : 0);
                row.createCell(7).setCellValue(dto.statusName());
                row.createCell(8).setCellValue(dto.createdAt());
                row.createCell(9).setCellValue(dto.deadline());
            }

            // 3. 열 너비 자동 조정
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("KPI 엑셀 생성 실패", e);
        }
    }
}
