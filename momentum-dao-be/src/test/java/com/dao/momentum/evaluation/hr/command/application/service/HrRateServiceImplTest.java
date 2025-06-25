package com.dao.momentum.evaluation.hr.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrRateCreateDTO;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrRateUpdateDTO;
import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrRate;
import com.dao.momentum.evaluation.hr.command.domain.repository.HrRateRepository;
import com.dao.momentum.evaluation.hr.exception.HrException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class HrRateServiceImplTest {

    private HrRateRepository hrRateRepository;
    private HrRateServiceImpl service;

    @BeforeEach
    void setUp() {
        hrRateRepository = mock(HrRateRepository.class);
        service = new HrRateServiceImpl(hrRateRepository);
    }

    @Test
    @DisplayName("등급 비율 생성 - 성공")
    void create_success() {
        HrRateCreateDTO dto = HrRateCreateDTO.builder()
                .rateS(20).rateA(20).rateB(20).rateC(20).rateD(20)
                .build();

        assertThatCode(() -> service.create(1, dto)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("등급 비율 생성 - 합계 100 아님 → 예외")
    void create_invalidSum() {
        HrRateCreateDTO dto = HrRateCreateDTO.builder()
                .rateS(10).rateA(10).rateB(10).rateC(10).rateD(10)
                .build();

        assertThatThrownBy(() -> service.create(1, dto))
                .isInstanceOf(HrException.class)
                .hasMessageContaining(ErrorCode.HR_RATE_INVALID_SUM.getMessage());
    }

    @Test
    @DisplayName("등급 비율 수정 - 성공")
    void update_success() {
        HrRate rate = HrRate.builder()
                .roundId(1).rateS(10).rateA(20).rateB(20).rateC(20).rateD(30)
                .build();

        HrRateUpdateDTO dto = HrRateUpdateDTO.builder()
                .rateS(20).rateA(20).rateB(20).rateC(20).rateD(20)
                .build();

        given(hrRateRepository.findByRoundId(1)).willReturn(Optional.of(rate));

        assertThatCode(() -> service.update(1, dto)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("등급 비율 수정 - 존재하지 않는 roundId → 예외")
    void update_notFound() {
        HrRateUpdateDTO dto = HrRateUpdateDTO.builder()
                .rateS(20).rateA(20).rateB(20).rateC(20).rateD(20)
                .build();

        given(hrRateRepository.findByRoundId(999)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999, dto))
                .isInstanceOf(HrException.class)
                .hasMessageContaining(ErrorCode.HR_RATE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("등급 비율 수정 - 합계 100 아님 → 예외")
    void update_invalidSum() {
        HrRate rate = HrRate.builder().roundId(1).build();
        HrRateUpdateDTO dto = HrRateUpdateDTO.builder()
                .rateS(10).rateA(10).rateB(10).rateC(10).rateD(10)
                .build();

        given(hrRateRepository.findByRoundId(1)).willReturn(Optional.of(rate));

        assertThatThrownBy(() -> service.update(1, dto))
                .isInstanceOf(HrException.class)
                .hasMessageContaining(ErrorCode.HR_RATE_INVALID_SUM.getMessage());
    }

    @Test
    @DisplayName("등급 비율 삭제 - 성공")
    void delete_success() {
        // given
        int roundId = 1;

        // when
        service.deleteByRoundId(roundId);

        // then
        then(hrRateRepository).should().deleteByRoundId(roundId);
    }

}
