package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalFactorScoreDto;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalScore;
import com.dao.momentum.evaluation.eval.command.domain.repository.EvalScoreRepository;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Nested
    @DisplayName("평가 요소 점수 저장 (Save Factor Scores)")
    class SaveFactorScores {

        @Test
        @DisplayName("평가 요소 점수 저장 - 성공")
        void saveFactorScores_success() {
            Long resultId = 100L;
            List<EvalFactorScoreDto> factorScores = List.of(
                    createDto(1, 85),
                    createDto(2, 90)
            );

            service.saveFactorScores(resultId, factorScores);

            then(evalScoreRepository).should(times(1)).saveAll(anyList());
        }

        @Test
        @DisplayName("resultId가 null이면 예외 발생")
        void saveFactorScores_fail_nullResultId() {
            List<EvalFactorScoreDto> factorScores = List.of(createDto(1, 80));

            assertThatThrownBy(() -> service.saveFactorScores(null, factorScores))
                    .isInstanceOf(EvalException.class)
                    .hasMessageContaining(ErrorCode.INVALID_RESULT_REQUEST.getMessage());
        }

        @Test
        @DisplayName("factorScores가 null이면 예외 발생")
        void saveFactorScores_fail_nullFactors() {
            Long resultId = 100L;

            assertThatThrownBy(() -> service.saveFactorScores(resultId, null))
                    .isInstanceOf(EvalException.class)
                    .hasMessageContaining(ErrorCode.EVAL_INVALID_NOT_EXIST.getMessage());
        }

        @Test
        @DisplayName("factorScores가 비어있으면 예외 발생")
        void saveFactorScores_fail_emptyFactors() {
            Long resultId = 100L;

            assertThatThrownBy(() -> service.saveFactorScores(resultId, new ArrayList<>()))
                    .isInstanceOf(EvalException.class)
                    .hasMessageContaining(ErrorCode.EVAL_INVALID_NOT_EXIST.getMessage());
        }
    }

    @Nested
    @DisplayName("단일 점수 저장 (Save Single Score)")
    class SaveSingleScore {

        @Test
        @DisplayName("단일 EvalScore 저장 - 성공")
        void save_singleScore_success() {
            EvalScore score = EvalScore.builder()
                    .resultId(100L)
                    .propertyId(1)
                    .score(90)
                    .build();

            service.save(score);

            then(evalScoreRepository).should(times(1)).save(score);
        }
    }

    @Nested
    @DisplayName("점수 갱신 (Update Scores)")
    class UpdateScores {

        @Test
        @DisplayName("모든 점수 삭제 후 다시 저장 - updateScores 성공")
        void updateScores_success() {
            Long resultId = 123L;
            var scoreMap = Map.of(
                    1, 80,
                    2, 90
            );

            service.updateScores(resultId, scoreMap);

            then(evalScoreRepository).should(times(1)).deleteByResultId(resultId);
            then(evalScoreRepository).should(times(2)).save(any(EvalScore.class)); // 2개 점수 저장
        }
    }

    @Nested
    @DisplayName("점수 삭제 (Delete Scores by ResultId)")
    class DeleteScores {

        @Test
        @DisplayName("결과 ID로 점수 전체 삭제 - 성공")
        void deleteByResultId_success() {
            Long resultId = 321L;

            service.deleteByResultId(resultId);

            then(evalScoreRepository).should().deleteByResultId(resultId);
        }
    }

    @Nested
    @DisplayName("일괄 점수 저장 (Save All Scores)")
    class SaveAllScores {

        @Test
        @DisplayName("여러 점수 리스트 저장 - saveAllScores 성공")
        void saveAll_scores_success() {
            List<EvalScore> scores = List.of(
                    EvalScore.builder().resultId(1L).propertyId(1).score(80).build(),
                    EvalScore.builder().resultId(1L).propertyId(2).score(70).build()
            );

            service.saveAll(scores);

            then(evalScoreRepository).should().saveAll(scores);
        }
    }

    // util
    private EvalFactorScoreDto createDto(int propertyId, int score) {
        return new EvalFactorScoreDto(propertyId, score);
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
