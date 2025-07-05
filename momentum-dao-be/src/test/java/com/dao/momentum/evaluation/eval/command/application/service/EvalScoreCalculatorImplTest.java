package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalFactorScoreDto;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalSubmitRequest;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrWeight;
import com.dao.momentum.evaluation.hr.command.domain.repository.HrWeightRepository;
import com.dao.momentum.evaluation.hr.exception.HrException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class EvalScoreCalculatorImplTest {

    private HrWeightRepository hrWeightRepository;
    private EvalScoreCalculatorImpl calculator;

    @BeforeEach
    void setUp() {
        hrWeightRepository = mock(HrWeightRepository.class);
        calculator = new EvalScoreCalculatorImpl(hrWeightRepository);
    }

    @Test
    @DisplayName("가중치 계산 방식 - 성공")
    void calculateScore_withWeights_success() {
        // Arrange
        EvalSubmitRequest request = new EvalSubmitRequest();
        setField(request, "formId", 4);
        setField(request, "roundId", 1);

        List<EvalFactorScoreDto> factors = new ArrayList<>();
        factors.add(createFactorScore(1, 80));
        factors.add(createFactorScore(2, 90));
        factors.add(createFactorScore(3, 70));
        factors.add(createFactorScore(4, 60));
        factors.add(createFactorScore(5, 100));
        factors.add(createFactorScore(6, 50));
        setField(request, "factorScores", factors);

        HrWeight weight = HrWeight.builder()
                .performWt(10)
                .teamWt(20)
                .attitudeWt(10)
                .growthWt(20)
                .engagementWt(20)
                .resultWt(20)
                .build();

        given(hrWeightRepository.findByRoundId(1)).willReturn(Optional.of(weight));

        // Act
        int score = calculator.calculateScore(1001L, request);

        // Assert
        assertThat(score).isEqualTo(75); // 계산 공식 결과
    }

    @Test
    @DisplayName("평균 계산 방식 - 성공")
    void calculateScore_simpleAverage_success() {
        // Arrange
        EvalSubmitRequest request = new EvalSubmitRequest();
        setField(request, "formId", 2);
        List<EvalFactorScoreDto> factors = new ArrayList<>();
        factors.add(createFactorScore(1, 80));
        factors.add(createFactorScore(2, 90));
        factors.add(createFactorScore(3, 70));
        setField(request, "factorScores", factors);

        // Act
        int score = calculator.calculateScore(1002L, request);

        // Assert
        assertThat(score).isEqualTo(80);
    }

    @Test
    @DisplayName("factorScores가 비어있으면 예외 발생")
    void calculateScore_noFactors_fail() {
        // Arrange
        EvalSubmitRequest request = new EvalSubmitRequest();
        setField(request, "formId", 4);
        setField(request, "factorScores", List.of());

        // Act & Assert
        assertThatThrownBy(() -> calculator.calculateScore(1003L, request))
                .isInstanceOf(EvalException.class)
                .hasMessageContaining(ErrorCode.EVAL_INVALID_NOT_EXIST.getMessage());
    }

    @Test
    @DisplayName("가중치가 존재하지 않으면 예외 발생")
    void calculateScore_missingWeights_fail() {
        // given
        EvalSubmitRequest request = new EvalSubmitRequest();
        setField(request, "formId", 4);
        setField(request, "roundId", 99);
        List<EvalFactorScoreDto> factors = new ArrayList<>();
        factors.add(createFactorScore(1, 80));
        setField(request, "factorScores", factors);

        given(hrWeightRepository.findByRoundId(99)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> calculator.calculateScore(1004L, request))
                .isInstanceOf(EvalException.class)  // Expecting EvalException here
                .hasMessageContaining(ErrorCode.EVAL_SCORE_CALCULATION_FAILED.getMessage());  // You can match the message here
    }

    private EvalFactorScoreDto createFactorScore(int propertyId, int score) {
        EvalFactorScoreDto dto = new EvalFactorScoreDto();
        setField(dto, "propertyId", propertyId);
        setField(dto, "score", score);
        return dto;
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("리플렉션 주입 실패: " + fieldName, e);
        }
    }

    @Test
    @DisplayName("calculateOverallScore - 점수 계산 성공")
    void calculateOverallScore_success() {
        // Arrange
        Map<Integer, Integer> scoreMap = Map.of(
                1, 80,  // perform
                2, 90,  // team
                3, 70,  // attitude
                4, 60,  // growth
                5, 100, // engagement
                6, 50   // result
        );

        HrWeight weight = HrWeight.builder()
                .performWt(10)
                .teamWt(20)
                .attitudeWt(10)
                .growthWt(20)
                .engagementWt(20)
                .resultWt(20)
                .build();

        // Act
        int finalScore = calculator.calculateOverallScore(scoreMap, weight);

        // Assert
        assertThat(finalScore).isEqualTo(75); // 계산 공식과 동일
    }

    @Test
    @DisplayName("calculateOverallScore - 일부 키 누락 시 기본값 0 처리")
    void calculateOverallScore_missingFactors_defaultsToZero() {
        // Arrange
        Map<Integer, Integer> scoreMap = Map.of(
                1, 80,
                2, 90 // 나머지 3~6번 요인 없음 → 0으로 처리
        );

        HrWeight weight = HrWeight.builder()
                .performWt(10)
                .teamWt(20)
                .attitudeWt(10)
                .growthWt(20)
                .engagementWt(20)
                .resultWt(20)
                .build();

        // Act
        int finalScore = calculator.calculateOverallScore(scoreMap, weight);

        // Assert
        // 계산: (80*10 + 90*20 + 0 + 0 + 0 + 0)/100 = (800 + 1800) / 100 = 26
        assertThat(finalScore).isEqualTo(26);
    }

}
