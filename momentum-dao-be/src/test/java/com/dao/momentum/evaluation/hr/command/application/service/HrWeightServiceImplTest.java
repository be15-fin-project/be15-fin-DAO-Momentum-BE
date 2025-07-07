package com.dao.momentum.evaluation.hr.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrWeightCreateDTO;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrWeightUpdateDTO;
import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrWeight;
import com.dao.momentum.evaluation.hr.command.domain.repository.HrWeightRepository;
import com.dao.momentum.evaluation.hr.exception.HrException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class HrWeightServiceImplTest {

    private HrWeightRepository hrWeightRepository;
    private HrWeightServiceImpl service;

    @BeforeEach
    void setUp() {
        hrWeightRepository = mock(HrWeightRepository.class);
        service = new HrWeightServiceImpl(hrWeightRepository);
    }

    @Nested
    @DisplayName("가중치 생성")
    class Create {

        @Test
        @DisplayName("성공")
        void create_success() {
            HrWeightCreateDTO dto = HrWeightCreateDTO.builder()
                    .performWt(10).teamWt(10).attitudeWt(20)
                    .growthWt(20).engagementWt(20).resultWt(20)
                    .build();

            assertThatCode(() -> service.create(1, dto)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("합계 100 아님 → 예외")
        void create_invalidSum() {
            HrWeightCreateDTO dto = HrWeightCreateDTO.builder()
                    .performWt(10).teamWt(10).attitudeWt(10)
                    .growthWt(10).engagementWt(10).resultWt(10)
                    .build();

            assertThatThrownBy(() -> service.create(1, dto))
                    .isInstanceOf(HrException.class)
                    .hasMessageContaining(ErrorCode.HR_WEIGHT_INVALID_SUM.getMessage());
        }
    }

    @Nested
    @DisplayName("가중치 수정")
    class Update {

        @Test
        @DisplayName("성공")
        void update_success() {
            HrWeight weight = HrWeight.builder().roundId(1).build();

            HrWeightUpdateDTO dto = HrWeightUpdateDTO.builder()
                    .performWt(10).teamWt(10).attitudeWt(20)
                    .growthWt(20).engagementWt(20).resultWt(20)
                    .build();

            given(hrWeightRepository.findByRoundId(1)).willReturn(Optional.of(weight));

            assertThatCode(() -> service.update(1, dto)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("존재하지 않는 roundId → 예외")
        void update_notFound() {
            HrWeightUpdateDTO dto = HrWeightUpdateDTO.builder()
                    .performWt(10).teamWt(10).attitudeWt(20)
                    .growthWt(20).engagementWt(20).resultWt(20)
                    .build();

            given(hrWeightRepository.findByRoundId(999)).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(999, dto))
                    .isInstanceOf(HrException.class)
                    .hasMessageContaining(ErrorCode.HR_WEIGHT_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("합계 100 아님 → 예외")
        void update_invalidSum() {
            HrWeight weight = HrWeight.builder().roundId(1).build();

            HrWeightUpdateDTO dto = HrWeightUpdateDTO.builder()
                    .performWt(10).teamWt(10).attitudeWt(10)
                    .growthWt(10).engagementWt(10).resultWt(10)
                    .build();

            given(hrWeightRepository.findByRoundId(1)).willReturn(Optional.of(weight));

            assertThatThrownBy(() -> service.update(1, dto))
                    .isInstanceOf(HrException.class)
                    .hasMessageContaining(ErrorCode.HR_WEIGHT_INVALID_SUM.getMessage());
        }
    }

    @Nested
    @DisplayName("가중치 삭제")
    class Delete {

        @Test
        @DisplayName("성공")
        void delete_success() {
            // given
            int roundId = 1;

            // when
            service.deleteByRoundId(roundId);

            // then
            then(hrWeightRepository).should().deleteByRoundId(roundId);
        }
    }
}
