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

    @Test
    @DisplayName("평가 회차 등록 - 성공")
    void create_success() {
        // given
        EvalRoundCreateDTO dto = EvalRoundCreateDTO.builder()
                .roundNo(10)
                .startAt(LocalDate.of(2025, 7, 1))
                .build();

        EvalRound saved = EvalRound.builder()
                .roundId(1)
                .roundNo(dto.getRoundNo())
                .startAt(dto.getStartAt())
                .build();

        given(evalRoundRepository.save(any(EvalRound.class))).willReturn(saved);

        // when
        EvalRound result = evalRoundService.create(dto);

        // then
        assertThat(result.getRoundId()).isEqualTo(1);
        assertThat(result.getRoundNo()).isEqualTo(10);
        assertThat(result.getStartAt()).isEqualTo(LocalDate.of(2025, 7, 1));
        then(evalRoundRepository).should().save(any(EvalRound.class));
    }

    @Test
    @DisplayName("평가 회차 수정 - 성공")
    void update_success() {
        // given
        int roundId = 999;
        EvalRoundUpdateDTO dto = EvalRoundUpdateDTO.builder()
                .roundId(roundId)
                .roundNo(5)
                .startAt(LocalDate.of(2025, 7, 2))
                .build();

        EvalRound found = EvalRound.builder()
                .roundId(roundId)
                .roundNo(3)
                .startAt(LocalDate.of(2025, 6, 1))
                .build();

        given(evalRoundRepository.findById(roundId)).willReturn(Optional.of(found));

        // when
        EvalRoundUpdateResponse response = evalRoundService.update(roundId, dto);

        // then
        assertThat(response.getRoundId()).isEqualTo(roundId);
        assertThat(response.getMessage()).contains("성공적으로 수정");

        then(evalRoundRepository).should().findById(roundId);
    }

    @Test
    @DisplayName("평가 회차 수정 - 회차 없음 예외")
    void update_notFound() {
        // given
        int roundId = 404;
        EvalRoundUpdateDTO dto = EvalRoundUpdateDTO.builder()
                .roundId(roundId)
                .roundNo(7)
                .startAt(LocalDate.of(2025, 8, 1))
                .build();

        given(evalRoundRepository.findById(roundId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> evalRoundService.update(roundId, dto))
                .isInstanceOf(EvalException.class)
                .hasMessageContaining(ErrorCode.EVAL_ROUND_NOT_FOUND.getMessage());

        then(evalRoundRepository).should().findById(roundId);
    }
}
