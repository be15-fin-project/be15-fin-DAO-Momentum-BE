package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalSubmitRequest;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalResponse;
import com.dao.momentum.evaluation.eval.command.domain.repository.EvalResponseRepository;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

class EvalResponseServiceImplTest {

    private EvalResponseRepository evalResponseRepository;
    private EvalResponseServiceImpl service;

    @BeforeEach
    void setUp() {
        evalResponseRepository = mock(EvalResponseRepository.class);
        service = new EvalResponseServiceImpl(evalResponseRepository);
    }

    @Test
    @DisplayName("평가 결과 저장 - 성공 (targetId 없음)")
    void saveResponse_success_withoutTargetId() {
        Long empId = 1001L;
        EvalSubmitRequest request = new EvalSubmitRequest();
        setField(request, "roundId", 1);
        setField(request, "formId", 4);
        setField(request, "targetId", null);
        setField(request, "reason", "테스트 사유");

        given(evalResponseRepository.existsByRoundIdAndFormIdAndEvalIdAndTargetIdIsNull(1, 4, empId))
                .willReturn(false);

        EvalResponse mockSaved = EvalResponse.builder()
                .resultId(999L)
                .roundId(1)
                .formId(4)
                .evalId(empId)
                .targetId(null)
                .score(85)
                .reason("테스트 사유")
                .createdAt(LocalDateTime.now())
                .build();

        given(evalResponseRepository.save(any(EvalResponse.class))).willReturn(mockSaved);

        EvalResponse result = service.saveResponse(empId, request, 85);

        assertThat(result.getEvalId()).isEqualTo(empId);
        assertThat(result.getScore()).isEqualTo(85);
        assertThat(result.getTargetId()).isNull();
    }

    @Test
    @DisplayName("평가 결과 저장 - 실패 (이미 제출됨)")
    void saveResponse_fail_alreadySubmitted() {
        Long empId = 1001L;
        EvalSubmitRequest request = new EvalSubmitRequest();
        setField(request, "roundId", 1);
        setField(request, "formId", 4);
        setField(request, "targetId", 2002L);
        setField(request, "reason", "중복 테스트");

        given(evalResponseRepository.existsByRoundIdAndFormIdAndEvalIdAndTargetId(1, 4, empId, 2002L))
                .willReturn(true);

        assertThatThrownBy(() -> service.saveResponse(empId, request, 90))
                .isInstanceOf(EvalException.class)
                .hasMessageContaining(ErrorCode.EVAL_ALREADY_SUBMITTED.getMessage());
    }

    @Test
    @DisplayName("평가 결과 점수 및 사유 수정 - 성공")
    void updateFinalScoreAndReason_success() {
        Long resultId = 10L;
        EvalResponse response = EvalResponse.builder()
                .resultId(resultId)
                .score(50)
                .reason("이전 이유")
                .build();

        given(evalResponseRepository.findById(resultId)).willReturn(Optional.of(response));

        service.updateFinalScoreAndReason(resultId, 95, "수정된 이유");

        assertThat(response.getScore()).isEqualTo(95);
        assertThat(response.getReason()).isEqualTo("수정된 이유");
    }

    @Test
    @DisplayName("평가 결과 점수 및 사유 수정 - 평가 없음 예외")
    void updateFinalScoreAndReason_notFound() {
        given(evalResponseRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateFinalScoreAndReason(999L, 80, "업데이트"))
                .isInstanceOf(EvalException.class)
                .hasMessageContaining(ErrorCode.EVALUATION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("ResultId로 RoundId 조회 - 성공")
    void getRoundIdByResultId_success() {
        given(evalResponseRepository.findRoundIdByResultId(77L)).willReturn(Optional.of(5));

        Integer roundId = service.getRoundIdByResultId(77L);
        assertThat(roundId).isEqualTo(5);
    }

    @Test
    @DisplayName("ResultId로 RoundId 조회 - 실패")
    void getRoundIdByResultId_fail() {
        given(evalResponseRepository.findRoundIdByResultId(77L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.getRoundIdByResultId(77L))
                .isInstanceOf(EvalException.class)
                .hasMessageContaining(ErrorCode.EVALUATION_NOT_FOUND.getMessage());
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
