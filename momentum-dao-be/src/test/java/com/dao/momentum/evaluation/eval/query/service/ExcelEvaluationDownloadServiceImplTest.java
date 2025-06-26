package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import com.dao.momentum.evaluation.eval.query.dto.request.PeerEvaluationExcelRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.PeerEvaluationExcelDto;
import com.dao.momentum.evaluation.eval.query.mapper.ExcelEvaluationQueryMapper;
import com.dao.momentum.evaluation.eval.query.util.PeerEvaluationExcelGenerator;
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
class ExcelEvaluationDownloadServiceImplTest {

    @Mock
    private ExcelEvaluationQueryMapper excelEvaluationQueryMapper;

    @InjectMocks
    private ExcelEvaluationDownloadServiceImpl service;

    @Test
    @DisplayName("사원 간 평가 엑셀 다운로드 - 성공 케이스")
    void downloadPeerEvaluationExcel_success() {
        // given
        PeerEvaluationExcelRequestDto request = new PeerEvaluationExcelRequestDto();
        PeerEvaluationExcelDto mockDto = PeerEvaluationExcelDto.builder()
                .roundNo("1차")
                .targetEmpNo("20240001")
                .targetName("홍길동")
                .departmentName("인사팀")
                .positionName("사원")
                .evaluatorEmpNo("20250001")
                .evaluatorName("김철수")
                .score(85)
                .submittedAt("2025-06-20 13:00")
                .build();

        List<PeerEvaluationExcelDto> mockList = List.of(mockDto);
        byte[] mockExcel = "mock-excel".getBytes();

        when(excelEvaluationQueryMapper.selectPeerEvaluationsForExcel(request))
                .thenReturn(mockList);

        try (MockedStatic<PeerEvaluationExcelGenerator> mockStatic = mockStatic(PeerEvaluationExcelGenerator.class)) {
            mockStatic.when(() -> PeerEvaluationExcelGenerator.generate(mockList))
                    .thenReturn(mockExcel);

            // when
            byte[] result = service.downloadPeerEvaluationExcel(request);

            // then
            assertThat(result).isEqualTo(mockExcel);
            verify(excelEvaluationQueryMapper).selectPeerEvaluationsForExcel(request);
            mockStatic.verify(() -> PeerEvaluationExcelGenerator.generate(mockList));
        }
    }

    @Test
    @DisplayName("사원 간 평가 엑셀 다운로드 - 데이터 없음 예외")
    void downloadPeerEvaluationExcel_noData() {
        // given
        PeerEvaluationExcelRequestDto request = new PeerEvaluationExcelRequestDto();
        when(excelEvaluationQueryMapper.selectPeerEvaluationsForExcel(request))
                .thenReturn(List.of());

        // when & then
        assertThatThrownBy(() -> service.downloadPeerEvaluationExcel(request))
                .isInstanceOf(EvalException.class)
                .hasMessageContaining(ErrorCode.EVALUATION_RESULT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("사원 간 평가 엑셀 다운로드 - Excel 생성 실패 예외")
    void downloadPeerEvaluationExcel_generationFailed() {
        // given
        PeerEvaluationExcelRequestDto request = new PeerEvaluationExcelRequestDto();
        List<PeerEvaluationExcelDto> mockList = List.of(
                PeerEvaluationExcelDto.builder()
                        .roundNo("1차")
                        .targetEmpNo("20240001")
                        .targetName("홍길동")
                        .departmentName("인사팀")
                        .positionName("사원")
                        .evaluatorEmpNo("20250001")
                        .evaluatorName("김철수")
                        .score(85)
                        .submittedAt("2025-06-20 13:00")
                        .build()
        );

        when(excelEvaluationQueryMapper.selectPeerEvaluationsForExcel(request))
                .thenReturn(mockList);

        try (MockedStatic<PeerEvaluationExcelGenerator> mockStatic = mockStatic(PeerEvaluationExcelGenerator.class)) {
            mockStatic.when(() -> PeerEvaluationExcelGenerator.generate(mockList))
                    .thenThrow(new RuntimeException("Excel 오류"));

            // when & then
            assertThatThrownBy(() -> service.downloadPeerEvaluationExcel(request))
                    .isInstanceOf(EvalException.class)
                    .hasMessageContaining(ErrorCode.EXCEL_GENERATION_FAILED.getMessage());
        }
    }
}
