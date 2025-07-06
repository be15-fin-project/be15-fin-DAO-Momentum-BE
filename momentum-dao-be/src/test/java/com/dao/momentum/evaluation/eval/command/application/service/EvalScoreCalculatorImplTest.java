package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalFactorScoreDto;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalSubmitRequest;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrWeight;
import com.dao.momentum.evaluation.hr.command.domain.repository.HrWeightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class EvalScoreCalculatorImplTest {

    private HrWeightRepository hrWeightRepository;
    private EvalScoreCalculatorImpl calculator;

    @BeforeEach
    void setUp() {
        hrWeightRepository = mock(HrWeightRepository.class);
        calculator = new EvalScoreCalculatorImpl(hrWeightRepository);
    }

    @Nested
    @DisplayName("가중치 계산 방식 (Score Calculation with Weights)")
    class ScoreCalculationWithWeights {

        @Test
        @DisplayName("가중치 계산 방식 - 성공")
        void calculateScore_withWeights_success() {
            // Arrange
            EvalSubmitRequest request = new EvalSubmitRequest(1, 4, 1003L, "Test Reason", List.of(
                    new EvalFactorScoreDto(1, 80),
                    new EvalFactorScoreDto(2, 90),
                    new EvalFactorScoreDto(3, 70),
                    new EvalFactorScoreDto(4, 60),
                    new EvalFactorScoreDto(5, 100),
                    new EvalFactorScoreDto(6, 50)
            ));

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
        @DisplayName("가중치가 존재하지 않으면 예외 발생")
        void calculateScore_missingWeights_fail() {
            // Arrange
            EvalSubmitRequest request = new EvalSubmitRequest(1, 4, 1003L, "Test Reason", List.of(
                    new EvalFactorScoreDto(1, 80)
            ));

            given(hrWeightRepository.findByRoundId(99)).willReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> calculator.calculateScore(1004L, request))
                    .isInstanceOf(EvalException.class)
                    .extracting("errorCode") // errorCode를 추출하여 비교
                    .isEqualTo(ErrorCode.EVAL_SCORE_CALCULATION_FAILED);  // 예상하는 errorCode와 비교
        }
    }

    @Nested
    @DisplayName("평균 계산 방식 (Simple Average Calculation)")
    class SimpleAverageCalculation {

        @Test
        @DisplayName("평균 계산 방식 - 성공")
        void calculateScore_simpleAverage_success() {
            // Arrange
            EvalSubmitRequest request = new EvalSubmitRequest(2, 2, 1002L, "Test Reason", List.of(
                    new EvalFactorScoreDto(1, 80),
                    new EvalFactorScoreDto(2, 90),
                    new EvalFactorScoreDto(3, 70)
            ));

            // Act
            int score = calculator.calculateScore(1002L, request);

            // Assert
            assertThat(score).isEqualTo(80);
        }

        @Test
        @DisplayName("factorScores가 비어있으면 예외 발생")
        void calculateScore_noFactors_fail() {
            // Arrange
            EvalSubmitRequest request = new EvalSubmitRequest(4, 4, 1003L, "Test Reason", List.of());

            // Act & Assert
            assertThatThrownBy(() -> calculator.calculateScore(1003L, request))
                    .isInstanceOf(EvalException.class)
                    .extracting("errorCode") // errorCode를 추출하여 비교
                    .isEqualTo(ErrorCode.EVAL_INVALID_NOT_EXIST);  // 예상하는 errorCode와 비교
        }
    }

    @Nested
    @DisplayName("종합 점수 계산 (Overall Score Calculation)")
    class OverallScoreCalculation {

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
}
