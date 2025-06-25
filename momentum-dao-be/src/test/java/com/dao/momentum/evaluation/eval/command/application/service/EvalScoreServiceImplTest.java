package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalFactorScoreDto;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalScore;
import com.dao.momentum.evaluation.eval.command.domain.repository.EvalScoreRepository;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class EvalScoreServiceImplTest {

    private EvalScoreRepository evalScoreRepository;
    private EvalScoreServiceImpl service;

    @BeforeEach
    void setUp() {
        evalScoreRepository = mock(EvalScoreRepository.class);
        service = new EvalScoreServiceImpl(evalScoreRepository);
    }

    @Test
    @DisplayName("평가 요소 점수 저장 - 성공")
    void saveFactorScores_success() {
        // given
        Long resultId = 100L;
        List<EvalFactorScoreDto> factorScores = List.of(
                createDto(1, 85),
                createDto(2, 90)
        );

        // when
        service.saveFactorScores(resultId, factorScores);

        // then
        then(evalScoreRepository).should(times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("resultId가 null이면 예외 발생")
    void saveFactorScores_fail_nullResultId() {
        // given
        List<EvalFactorScoreDto> factorScores = List.of(createDto(1, 80));

        // when & then
        assertThatThrownBy(() -> service.saveFactorScores(null, factorScores))
                .isInstanceOf(EvalException.class)
                .hasMessageContaining(ErrorCode.INVALID_RESULT_REQUEST.getMessage());
    }

    @Test
    @DisplayName("factorScores가 null이면 예외 발생")
    void saveFactorScores_fail_nullFactors() {
        // given
        Long resultId = 100L;

        // when & then
        assertThatThrownBy(() -> service.saveFactorScores(resultId, null))
                .isInstanceOf(EvalException.class)
                .hasMessageContaining(ErrorCode.EVAL_INVALID_NOT_EXIST.getMessage());
    }

    @Test
    @DisplayName("factorScores가 비어있으면 예외 발생")
    void saveFactorScores_fail_emptyFactors() {
        // given
        Long resultId = 100L;

        // when & then
        assertThatThrownBy(() -> service.saveFactorScores(resultId, new ArrayList<>()))
                .isInstanceOf(EvalException.class)
                .hasMessageContaining(ErrorCode.EVAL_INVALID_NOT_EXIST.getMessage());
    }

    // util
    private EvalFactorScoreDto createDto(int propertyId, int score) {
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
