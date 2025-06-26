package com.dao.momentum.evaluation.kpi.query.service;

import com.dao.momentum.evaluation.kpi.query.dto.request.KpiListRequestDto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiExcelDownloadServiceImplTest {

    @Mock
    private KpiExcelMapper kpiExcelMapper;

    @InjectMocks
    private KpiExcelDownloadServiceImpl kpiExcelDownloadService;

    @Test
    @DisplayName("KPI 엑셀 다운로드 - 매퍼 조회 및 Excel 변환 성공")
    void downloadKpisAsExcel_success() {
        // given
        KpiListRequestDto requestDto = KpiListRequestDto.builder()
                .deptId(10)
                .startDate("2025-06-01")
                .endDate("2025-06-30")
                .build();

        List<KpiExcelDto> mockData = List.of(
                KpiExcelDto.builder()
                        .employeeNo("EMP001")
                        .employeeName("홍길동")
                        .departmentName("기획팀")
                        .positionName("과장")
                        .goal("매출 증대")
                        .goalValue(100)
                        .kpiProgress(80)
                        .statusName("ACCEPTED")
                        .createdAt("2025-06-01")
                        .deadline("2025-06-30")
                        .build()
        );

        byte[] mockExcel = "dummy-kpi-excel".getBytes();

        when(kpiExcelMapper.selectKpisForExcel(requestDto)).thenReturn(mockData);

        try (MockedStatic<KpiExcelGenerator> mockedStatic = mockStatic(KpiExcelGenerator.class)) {
            mockedStatic.when(() -> KpiExcelGenerator.generate(mockData)).thenReturn(mockExcel);

            // when
            byte[] result = kpiExcelDownloadService.downloadKpisAsExcel(requestDto);

            // then
            assertThat(result).isEqualTo(mockExcel);
            verify(kpiExcelMapper).selectKpisForExcel(requestDto);
            mockedStatic.verify(() -> KpiExcelGenerator.generate(mockData));
        }
    }
}
