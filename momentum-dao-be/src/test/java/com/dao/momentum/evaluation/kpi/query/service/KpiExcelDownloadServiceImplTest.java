package com.dao.momentum.evaluation.kpi.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.kpi.exception.KpiException;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiExelRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiExcelDto;
import com.dao.momentum.evaluation.kpi.query.mapper.KpiExcelMapper;
import com.dao.momentum.evaluation.kpi.query.util.KpiExcelGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiExcelDownloadServiceImplTest {

    @Mock
    private KpiExcelMapper kpiExcelMapper;

    @InjectMocks
    private KpiExcelDownloadServiceImpl service;

    @Test
    @DisplayName("KPI 엑셀 다운로드 - 성공 케이스")
    void downloadKpisAsExcel_success() {
        // given
        KpiExelRequestDto requestDto = KpiExelRequestDto.builder()
                .empNo("EMP001")
                .deptId(10)
                .startDate("2025-06-01")
                .endDate("2025-06-30")
                .build();

        List<KpiExcelDto> mockData = List.of(
                KpiExcelDto.builder()
                        .employeeNo("EMP001")
                        .employeeName("홍길동")
                        .departmentName("기획팀")
                        .goal("매출 증대")
                        .goalValue(100)
                        .kpiProgress(90)
                        .statusName("ACCEPTED")
                        .createdAt("2025-06-01")
                        .deadline("2025-06-30")
                        .build()
        );

        byte[] mockExcel = "dummy-excel".getBytes();

        when(kpiExcelMapper.selectKpisForExcel(requestDto)).thenReturn(mockData);

        try (MockedStatic<KpiExcelGenerator> mockedStatic = mockStatic(KpiExcelGenerator.class)) {
            mockedStatic.when(() -> KpiExcelGenerator.generate(mockData)).thenReturn(mockExcel);

            // when
            byte[] result = service.downloadKpisAsExcel(requestDto);

            // then
            assertThat(result).isEqualTo(mockExcel);
            verify(kpiExcelMapper).selectKpisForExcel(requestDto);
            mockedStatic.verify(() -> KpiExcelGenerator.generate(mockData));
        }
    }

    @Test
    @DisplayName("KPI 엑셀 다운로드 - 조회 결과 없음 예외")
    void downloadKpisAsExcel_noData_throwsException() {
        // given
        KpiExelRequestDto requestDto = KpiExelRequestDto.builder()
                .deptId(10)
                .build();

        when(kpiExcelMapper.selectKpisForExcel(requestDto)).thenReturn(List.of());

        // when & then
        assertThatThrownBy(() -> service.downloadKpisAsExcel(requestDto))
                .isInstanceOf(KpiException.class)
                .hasMessage(ErrorCode.KPI_LIST_NOT_FOUND.getMessage());

        verify(kpiExcelMapper).selectKpisForExcel(requestDto);
    }

    @Test
    @DisplayName("KPI 엑셀 다운로드 - Excel 생성 실패 예외")
    void downloadKpisAsExcel_excelGenerationFails_throwsException() {
        // given
        KpiExelRequestDto requestDto = KpiExelRequestDto.builder()
                .deptId(10)
                .build();

        List<KpiExcelDto> mockData = List.of(
                KpiExcelDto.builder().employeeNo("EMP001").build()
        );

        when(kpiExcelMapper.selectKpisForExcel(requestDto)).thenReturn(mockData);

        try (MockedStatic<KpiExcelGenerator> mockedStatic = mockStatic(KpiExcelGenerator.class)) {
            mockedStatic.when(() -> KpiExcelGenerator.generate(mockData))
                    .thenThrow(new RuntimeException("Excel error"));

            // when & then
            assertThatThrownBy(() -> service.downloadKpisAsExcel(requestDto))
                    .isInstanceOf(KpiException.class)
                    .hasMessage(ErrorCode.EXCEL_GENERATION_FAILED.getMessage());

            verify(kpiExcelMapper).selectKpisForExcel(requestDto);
            mockedStatic.verify(() -> KpiExcelGenerator.generate(mockData));
        }
    }
}
