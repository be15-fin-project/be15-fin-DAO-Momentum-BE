package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import com.dao.momentum.evaluation.eval.query.dto.request.PeerEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.PeerEvaluationListResultDto;
import com.dao.momentum.evaluation.eval.query.dto.response.PeerEvaluationResponseDto;
import com.dao.momentum.evaluation.eval.query.mapper.PeerEvaluationMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EvaluationQueryServiceImplTest {

    private final PeerEvaluationMapper peerEvaluationMapper = mock(PeerEvaluationMapper.class);
    private final EvaluationQueryServiceImpl evaluationQueryService = new EvaluationQueryServiceImpl(peerEvaluationMapper);

    @Test
    @DisplayName("사원 간 평가 결과 조회 성공")
    void getPeerEvaluations_success() {
        // given
        PeerEvaluationListRequestDto requestDto = new PeerEvaluationListRequestDto();
        requestDto.setPage(1);
        requestDto.setSize(10);

        PeerEvaluationResponseDto responseDto = PeerEvaluationResponseDto.builder()
                .resultId(1L)
                .evalId(20250001L)
                .evalName("김현우")
                .targetId(20250002L)
                .targetName("정예준")
                .formName("동료 평가")
                .roundNo(2)
                .score(85)
                .reason("협업이 뛰어남")
                .createdAt(LocalDateTime.now())
                .build();

        when(peerEvaluationMapper.countPeerEvaluations(requestDto)).thenReturn(1L);
        when(peerEvaluationMapper.findPeerEvaluations(requestDto)).thenReturn(List.of(responseDto));

        // when
        PeerEvaluationListResultDto result = evaluationQueryService.getPeerEvaluations(requestDto);

        // then
        assertThat(result.getList()).hasSize(1);
        assertThat(result.getPagination().getCurrentPage()).isEqualTo(1);
        assertThat(result.getPagination().getTotalItems()).isEqualTo(1);
        assertThat(result.getList().get(0).getEvalName()).isEqualTo("김현우");

        verify(peerEvaluationMapper, times(1)).countPeerEvaluations(requestDto);
        verify(peerEvaluationMapper, times(1)).findPeerEvaluations(requestDto);
    }

    @Test
    @DisplayName("사원 간 평가 결과가 없으면 예외 발생")
    void getPeerEvaluations_empty_throwsException() {
        // given
        PeerEvaluationListRequestDto requestDto = new PeerEvaluationListRequestDto();
        requestDto.setPage(1);
        requestDto.setSize(10);

        when(peerEvaluationMapper.countPeerEvaluations(requestDto)).thenReturn(0L);
        when(peerEvaluationMapper.findPeerEvaluations(requestDto)).thenReturn(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> evaluationQueryService.getPeerEvaluations(requestDto))
                .isInstanceOf(EvalException.class)
                .hasMessageContaining(ErrorCode.EVALUATION_RESULT_NOT_FOUND.getMessage());

        verify(peerEvaluationMapper).countPeerEvaluations(requestDto);
        verify(peerEvaluationMapper).findPeerEvaluations(requestDto);
    }
}
