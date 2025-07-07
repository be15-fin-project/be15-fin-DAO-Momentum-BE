package com.dao.momentum.evaluation.kpi.command.application.service;

import com.dao.momentum.common.dto.Status;
import com.dao.momentum.common.dto.UseStatus;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.kpi.command.application.dto.request.KpiCreateDTO;
import com.dao.momentum.evaluation.kpi.command.application.dto.request.KpiProgressUpdateRequest;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.CancelKpiResponse;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.KpiCreateResponse;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.KpiProgressUpdateResponse;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.KpiWithdrawResponse;
import com.dao.momentum.evaluation.kpi.command.domain.aggregate.Kpi;
import com.dao.momentum.evaluation.kpi.command.domain.repository.KpiRepository;
import com.dao.momentum.evaluation.kpi.exception.KpiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiCommandServiceImplTest {

    @Mock
    private KpiRepository kpiRepository;

    @InjectMocks
    private KpiCommandServiceImpl service;

    private final Long empId = 1L;
    private final Long kpiId = 100L;

    private KpiCreateDTO createDto() {
        return KpiCreateDTO.builder()
                .goal("목표")
                .goalValue(100)
                .deadline(LocalDate.of(2025, 12, 31))
                .kpiProgress(0)
                .progress25("25%")
                .progress50("50%")
                .progress75("75%")
                .progress100("100%")
                .build();
    }

    private Kpi buildKpi(Status status, UseStatus deleted, Long ownerId, int progress) {
        return Kpi.builder()
                .kpiId(kpiId)
                .empId(ownerId)
                .statusId(status.getId())
                .isDeleted(deleted)
                .kpiProgress(progress)
                .build();
    }

    @Nested
    @DisplayName("KPI 생성 테스트")
    class CreateTest {

        @Test
        void createKpi_success() {
            KpiCreateDTO dto = createDto();
            Kpi mockSaved = mock(Kpi.class);
            when(mockSaved.getKpiId()).thenReturn(kpiId);
            when(kpiRepository.save(any())).thenReturn(mockSaved);

            KpiCreateResponse response = service.createKpi(empId, dto);

            assertThat(response.kpiId()).isEqualTo(kpiId);
            assertThat(response.message()).contains("생성");
        }
    }

    @Nested
    @DisplayName("KPI 철회 테스트")
    class WithdrawTest {

        @Test
        void withdraw_success() {
            Kpi kpi = buildKpi(Status.PENDING, UseStatus.N, empId, 0);
            when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));

            KpiWithdrawResponse response = service.withdrawKpi(empId, kpiId);

            assertThat(response.kpiId()).isEqualTo(kpiId);
            assertThat(response.message()).contains("철회");
        }

        @Test
        void withdraw_notFound() {
            when(kpiRepository.findById(kpiId)).thenReturn(Optional.empty());
            assertThrows(KpiException.class, () -> service.withdrawKpi(empId, kpiId));
        }

        @Test
        void withdraw_forbidden() {
            Kpi kpi = buildKpi(Status.PENDING, UseStatus.N, 999L, 0);
            when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));
            assertThrows(KpiException.class, () -> service.withdrawKpi(empId, kpiId));
        }

        @Test
        void withdraw_invalidStatus() {
            Kpi kpi = buildKpi(Status.ACCEPTED, UseStatus.N, empId, 0);
            when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));
            assertThrows(KpiException.class, () -> service.withdrawKpi(empId, kpiId));
        }
    }

    @Nested
    @DisplayName("KPI 취소 테스트")
    class CancelTest {

        @Test
        void cancel_success() {
            Kpi kpi = buildKpi(Status.ACCEPTED, UseStatus.N, empId, 0);
            when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));
            when(kpiRepository.save(any())).thenReturn(kpi);

            CancelKpiResponse response = service.cancelKpi(empId, kpiId, "이유");

            assertThat(response.kpiId()).isEqualTo(kpiId);
            assertThat(response.message()).contains("취소");
        }

        @Test
        void cancel_notFound() {
            when(kpiRepository.findById(kpiId)).thenReturn(Optional.empty());
            assertThrows(KpiException.class, () -> service.cancelKpi(empId, kpiId, "이유"));
        }

        @Test
        void cancel_forbidden() {
            Kpi kpi = buildKpi(Status.ACCEPTED, UseStatus.N, 999L, 0);
            when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));
            assertThrows(KpiException.class, () -> service.cancelKpi(empId, kpiId, "이유"));
        }

        @Test
        void cancel_invalidStatus() {
            Kpi kpi = buildKpi(Status.PENDING, UseStatus.N, empId, 0);
            when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));
            assertThrows(KpiException.class, () -> service.cancelKpi(empId, kpiId, "이유"));
        }
    }

    @Nested
    @DisplayName("진척도 수정 테스트")
    class ProgressTest {

        @Test
        void update_success() {
            Kpi kpi = buildKpi(Status.ACCEPTED, UseStatus.N, empId, 10);
            when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));

            KpiProgressUpdateRequest req = KpiProgressUpdateRequest.builder().progress(80).build();
            KpiProgressUpdateResponse res = service.updateProgress(empId, kpiId, req);

            assertEquals(80, res.progress());
            assertEquals(kpiId, res.kpiId());
        }

        @Test
        void update_invalidProgress_negative() {
            Kpi kpi = buildKpi(Status.ACCEPTED, UseStatus.N, empId, 0);
            when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));

            KpiProgressUpdateRequest req = KpiProgressUpdateRequest.builder().progress(-1).build();
            assertThrows(KpiException.class, () -> service.updateProgress(empId, kpiId, req));
        }

        @Test
        void update_invalidProgress_over100() {
            Kpi kpi = buildKpi(Status.ACCEPTED, UseStatus.N, empId, 0);
            when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));

            KpiProgressUpdateRequest req = KpiProgressUpdateRequest.builder().progress(101).build();
            assertThrows(KpiException.class, () -> service.updateProgress(empId, kpiId, req));
        }

        @Test
        void update_forbidden() {
            Kpi kpi = buildKpi(Status.ACCEPTED, UseStatus.N, 999L, 0);
            when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));

            KpiProgressUpdateRequest req = KpiProgressUpdateRequest.builder().progress(50).build();
            assertThrows(KpiException.class, () -> service.updateProgress(empId, kpiId, req));
        }

        @Test
        void update_notFound() {
            when(kpiRepository.findById(kpiId)).thenReturn(Optional.empty());

            KpiProgressUpdateRequest req = KpiProgressUpdateRequest.builder().progress(50).build();
            assertThrows(KpiException.class, () -> service.updateProgress(empId, kpiId, req));
        }
    }
}
