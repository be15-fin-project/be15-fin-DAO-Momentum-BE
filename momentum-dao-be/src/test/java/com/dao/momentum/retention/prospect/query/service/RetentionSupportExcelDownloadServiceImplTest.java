package com.dao.momentum.retention.prospect.query.service;

import com.dao.momentum.retention.prospect.query.dto.response.RetentionSupportExcelDto;
import com.dao.momentum.retention.prospect.query.mapper.RetentionSupportExcelMapper;
import com.dao.momentum.retention.prospect.query.util.ExcelGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetentionSupportExcelDownloadServiceImplTest {

    @Mock
    private RetentionSupportExcelMapper excelMapper;

    @InjectMocks
    private RetentionSupportExcelDownloadServiceImpl service;

    @Test
    @DisplayName("근속지원 엑셀 다운로드 - 정상 조회 및 생성 성공")
    void downloadExcel_success() {
        // given
        Long roundId = 1L;
        Long deptId = 2L;
        Long positionId = 3L;
        String stabilityType = "STABLE";

        List<RetentionSupportExcelDto> mockData = List.of(
                RetentionSupportExcelDto.builder()
                        .employeeNo("EMP001")
                        .employeeName("홍길동")
                        .roundNo("2024-03")
                        .deptName("인사팀")
                        .positionName("대리")
                        .retentionScore(82.5)
                        .jobSatisfaction(80.0)
                        .compensationSatisfaction(75.0)
                        .relationSatisfaction(78.0)
                        .growthSatisfaction(70.0)
                        .tenure(3.5)
                        .wlbSatisfaction(85.0)
                        .build()
        );

        byte[] dummyExcel = "dummy-excel-content".getBytes();

        // ExcelGenerator가 static 유틸이라 가정하고 mockStatic 사용
        try (MockedStatic<ExcelGenerator> mockedStatic = mockStatic(ExcelGenerator.class)) {
            // mock mapper + static util
            when(excelMapper.selectSupportListForExcel(roundId, deptId, positionId, stabilityType))
                    .thenReturn(mockData);
            mockedStatic.when(() -> ExcelGenerator.generate(mockData)).thenReturn(dummyExcel);

            // when
            byte[] result = service.downloadExcel(roundId, deptId, positionId, stabilityType);

            // then
            assertThat(result).isEqualTo(dummyExcel);
            verify(excelMapper).selectSupportListForExcel(roundId, deptId, positionId, stabilityType);
            mockedStatic.verify(() -> ExcelGenerator.generate(mockData));
        }
    }
}
