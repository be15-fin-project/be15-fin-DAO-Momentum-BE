package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalRoundCreateDTO;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalRoundUpdateDTO;
import com.dao.momentum.evaluation.eval.command.application.dto.response.EvalRoundUpdateResponse;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalRound;
import com.dao.momentum.evaluation.eval.command.domain.repository.EvalRoundRepository;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class EvalRoundServiceImplTest {

    private EvalRoundRepository evalRoundRepository;
    private EvalRoundServiceImpl evalRoundService;

    @BeforeEach
    void setUp() {
        evalRoundRepository = mock(EvalRoundRepository.class);
        evalRoundService = new EvalRoundServiceImpl(evalRoundRepository);
    }

    @Nested
    @DisplayName("평가 회차 등록 (Create Evaluation Round)")
    class CreateEvalRound {

        @Test
        @DisplayName("평가 회차 등록 - 성공")
        void create_success() {
            EvalRoundCreateDTO dto = EvalRoundCreateDTO.builder()
                    .roundNo(10)
                    .startAt(LocalDate.now().plusDays(5))
                    .build();

            given(evalRoundRepository.existsByRoundNo(dto.roundNo())).willReturn(false);
            given(evalRoundRepository.save(any())).willAnswer(inv -> {
                EvalRound input = inv.getArgument(0);
                return EvalRound.builder()
                        .roundId(1)
                        .roundNo(input.getRoundNo())
                        .startAt(input.getStartAt())
                        .build();
            });

            EvalRound result = evalRoundService.create(dto);

            assertThat(result.getRoundId()).isEqualTo(1);
            assertThat(result.getRoundNo()).isEqualTo(10);
            assertThat(result.getStartAt()).isEqualTo(dto.startAt());
        }

        @Test
        @DisplayName("평가 회차 등록 - 중복 회차 번호 예외")
        void create_duplicateRoundNo_throwsException() {
            EvalRoundCreateDTO dto = EvalRoundCreateDTO.builder()
                    .roundNo(5)
                    .startAt(LocalDate.now().plusDays(2))
                    .build();

            given(evalRoundRepository.existsByRoundNo(5)).willReturn(true);

            assertThatThrownBy(() -> evalRoundService.create(dto))
                    .isInstanceOf(EvalException.class)
                    .hasMessageContaining(ErrorCode.EVAL_ROUND_DUPLICATE.getMessage());
        }

        @Test
        @DisplayName("평가 회차 등록 - 시작일 과거 예외")
        void create_invalidStartDate_throwsException() {
            EvalRoundCreateDTO dto = EvalRoundCreateDTO.builder()
                    .roundNo(11)
                    .startAt(LocalDate.now().minusDays(1))
                    .build();

            given(evalRoundRepository.existsByRoundNo(11)).willReturn(false);

            assertThatThrownBy(() -> evalRoundService.create(dto))
                    .isInstanceOf(EvalException.class)
                    .hasMessageContaining(ErrorCode.EVAL_ROUND_INVALID_START_DATE.getMessage());
        }
    }

    @Nested
    @DisplayName("평가 회차 수정 (Update Evaluation Round)")
    class UpdateEvalRound {

        @Test
        @DisplayName("평가 회차 수정 - 성공")
        void update_success() {
            int roundId = 999;
            EvalRoundUpdateDTO dto = EvalRoundUpdateDTO.builder()
                    .roundNo(5)
                    .startAt(LocalDate.now().plusDays(3))
                    .build();

            EvalRound existingRound = EvalRound.builder()
                    .roundId(roundId)
                    .roundNo(3)
                    .startAt(LocalDate.of(2025, 6, 1))
                    .build();

            given(evalRoundRepository.findById(roundId)).willReturn(Optional.of(existingRound));

            EvalRoundUpdateResponse response = evalRoundService.update(roundId, dto);

            assertThat(response.roundId()).isEqualTo(roundId);
            assertThat(response.message()).contains("성공적으로");
        }

        @Test
        @DisplayName("평가 회차 수정 - 회차 없음 예외")
        void update_notFound_throwsException() {
            int roundId = 404;
            EvalRoundUpdateDTO dto = EvalRoundUpdateDTO.builder()
                    .roundNo(7)
                    .startAt(LocalDate.now().plusDays(2))
                    .build();

            given(evalRoundRepository.findById(roundId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> evalRoundService.update(roundId, dto))
                    .isInstanceOf(EvalException.class)
                    .hasMessageContaining(ErrorCode.EVAL_ROUND_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("평가 회차 수정 - 시작일 오늘 또는 과거 예외")
        void update_invalidStartDate_throwsException() {
            int roundId = 999;
            EvalRoundUpdateDTO dto = EvalRoundUpdateDTO.builder()
                    .roundNo(6)
                    .startAt(LocalDate.now()) // 오늘은 허용되지 않음
                    .build();

            assertThatThrownBy(() -> evalRoundService.update(roundId, dto))
                    .isInstanceOf(EvalException.class)
                    .hasMessageContaining(ErrorCode.EVAL_ROUND_INVALID_START_DATE.getMessage());
        }
    }

    @Nested
    @DisplayName("평가 회차 삭제 (Delete Evaluation Round)")
    class DeleteEvalRound {

        @Test
        @DisplayName("평가 회차 삭제 - 성공")
        void delete_success() {
            int roundId = 1;

            given(evalRoundRepository.existsById(roundId)).willReturn(true);

            evalRoundService.delete(roundId);

            then(evalRoundRepository).should().deleteById(roundId);
        }

        @Test
        @DisplayName("평가 회차 삭제 - 회차 없음 예외")
        void delete_notFound_throwsException() {
            int roundId = 999;

            given(evalRoundRepository.existsById(roundId)).willReturn(false);

            assertThatThrownBy(() -> evalRoundService.delete(roundId))
                    .isInstanceOf(EvalException.class)
                    .hasMessageContaining(ErrorCode.EVAL_ROUND_NOT_FOUND.getMessage());
        }
    }
}
