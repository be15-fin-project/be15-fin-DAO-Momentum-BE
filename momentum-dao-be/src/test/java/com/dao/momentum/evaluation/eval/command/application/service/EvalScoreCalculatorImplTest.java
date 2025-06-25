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

        int score = calculator.calculateScore(1001L, request);

        assertThat(score).isEqualTo(75); // 계산 공식 결과
    }

    @Test
    @DisplayName("평균 계산 방식 - 성공")
    void calculateScore_simpleAverage_success() {
        EvalSubmitRequest request = new EvalSubmitRequest();
        setField(request, "formId", 2);
        List<EvalFactorScoreDto> factors = new ArrayList<>();
        factors.add(createFactorScore(1, 80));
        factors.add(createFactorScore(2, 90));
        factors.add(createFactorScore(3, 70));
        setField(request, "factorScores", factors);

        int score = calculator.calculateScore(1002L, request);

        assertThat(score).isEqualTo(80);
    }

    @Test
    @DisplayName("factorScores가 비어있으면 예외 발생")
    void calculateScore_noFactors_fail() {
        EvalSubmitRequest request = new EvalSubmitRequest();
        setField(request, "formId", 4);
        setField(request, "factorScores", List.of());

        assertThatThrownBy(() -> calculator.calculateScore(1003L, request))
                .isInstanceOf(EvalException.class)
                .hasMessageContaining(ErrorCode.EVAL_INVALID_NOT_EXIST.getMessage());
    }

    @Test
    @DisplayName("가중치가 존재하지 않으면 예외 발생")
    void calculateScore_missingWeights_fail() {
        EvalSubmitRequest request = new EvalSubmitRequest();
        setField(request, "formId", 4);
        setField(request, "roundId", 99);
        List<EvalFactorScoreDto> factors = new ArrayList<>();
        factors.add(createFactorScore(1, 80));
        setField(request, "factorScores", factors);

        given(hrWeightRepository.findByRoundId(99)).willReturn(Optional.empty());

        assertThatThrownBy(() -> calculator.calculateScore(1004L, request))
                .isInstanceOf(HrException.class)
                .hasMessageContaining(ErrorCode.HR_WEIGHT_NOT_FOUND.getMessage());
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
}
