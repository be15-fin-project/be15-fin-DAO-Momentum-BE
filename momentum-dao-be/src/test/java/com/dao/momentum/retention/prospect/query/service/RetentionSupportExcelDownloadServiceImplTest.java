package com.dao.momentum.retention.prospect.query.service;

import com.dao.momentum.retention.prospect.query.dto.request.RetentionSupportExcelDto;
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

    @Spy
    private ExcelGenerator excelGenerator = new ExcelGenerator(); // 정적 util이 아니면 Spy 가능

    @InjectMocks
    private RetentionSupportExcelDownloadServiceImpl service;

    @Test
    @DisplayName("근속지원 엑셀 다운로드 - 매퍼 조회 및 Excel 변환 성공")
    void downloadExcel_success() {
        // given
        Long roundId = 1L;
        Long deptId = 2L;
        String stabilityType = "STABLE";

        List<RetentionSupportExcelDto> mockData = List.of(
                new RetentionSupportExcelDto(
                        "EMP001", "홍길동", "2024-03", "인사팀",
                        82.5, 80.0, 75.0, 78.0, 70.0, 3.5, 85.0
                )
        );

        when(excelMapper.selectSupportListForExcel(roundId, deptId, stabilityType))
                .thenReturn(mockData);

        byte[] mockExcel = "dummy-excel-content".getBytes();

        // ExcelGenerator는 static util이라면 mock이 어려움 → 다음처럼 mockStatic 사용 필요
        try (MockedStatic<ExcelGenerator> mockedStatic = mockStatic(ExcelGenerator.class)) {
            mockedStatic.when(() -> ExcelGenerator.generate(mockData)).thenReturn(mockExcel);

            // when
            byte[] result = service.downloadExcel(roundId, deptId, stabilityType);

            // then
            assertThat(result).isEqualTo(mockExcel);
            verify(excelMapper).selectSupportListForExcel(roundId, deptId, stabilityType);
            mockedStatic.verify(() -> ExcelGenerator.generate(mockData));
        }
    }
}
