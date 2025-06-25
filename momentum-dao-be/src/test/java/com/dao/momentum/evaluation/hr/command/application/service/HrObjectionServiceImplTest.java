package com.dao.momentum.evaluation.hr.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrObjectionCreateDto;
import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrObjection;
import com.dao.momentum.evaluation.hr.command.domain.repository.HrObjectionRepository;
import com.dao.momentum.evaluation.hr.exception.HrException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class HrObjectionServiceImplTest {

    private HrObjectionRepository objectionRepository;
    private HrObjectionServiceImpl service;

    @BeforeEach
    void setUp() {
        objectionRepository = mock(HrObjectionRepository.class);
        service = new HrObjectionServiceImpl(objectionRepository);
    }

    @Test
    @DisplayName("이의제기 생성 - 성공")
    void create_success() {
        // given
        HrObjectionCreateDto dto = HrObjectionCreateDto.builder()
                .resultId(1L)
                .writerId(1001L)
                .reason("정당한 사유입니다.")
                .build();

        given(objectionRepository.existsByResultId(dto.getResultId())).willReturn(false);
        given(objectionRepository.existsEvaluation(dto.getResultId())).willReturn(true);

        HrObjection saved = HrObjection.builder()
                .objectionId(10L)
                .createdAt(LocalDateTime.now())
                .build();

        given(objectionRepository.save(any(HrObjection.class))).willReturn(saved);

        // when & then
        assertThatCode(() -> service.create(dto)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("이의제기 생성 - 이미 제출된 경우 예외")
    void create_duplicate() {
        // given
        HrObjectionCreateDto dto = HrObjectionCreateDto.builder()
                .resultId(1L)
                .writerId(1001L)
                .reason("중복 제출 테스트")
                .build();

        given(objectionRepository.existsByResultId(dto.getResultId())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(HrException.class)
                .hasMessageContaining(ErrorCode.ALREADY_SUBMITTED_OBJECTION.getMessage());
    }

    @Test
    @DisplayName("이의제기 생성 - 평가 결과 없음 → 예외")
    void create_evaluationNotFound() {
        // given
        HrObjectionCreateDto dto = HrObjectionCreateDto.builder()
                .resultId(1L)
                .writerId(1001L)
                .reason("결과 없음 테스트")
                .build();

        given(objectionRepository.existsByResultId(dto.getResultId())).willReturn(false);
        given(objectionRepository.existsEvaluation(dto.getResultId())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(HrException.class)
                .hasMessageContaining(ErrorCode.EVALUATION_NOT_FOUND.getMessage());
    }
}
