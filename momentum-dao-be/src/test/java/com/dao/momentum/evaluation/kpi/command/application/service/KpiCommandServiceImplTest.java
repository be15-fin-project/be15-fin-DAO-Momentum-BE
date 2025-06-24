package com.dao.momentum.evaluation.kpi.command.application.service;

import com.dao.momentum.common.dto.Status;
import com.dao.momentum.common.dto.UseStatus;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.kpi.command.application.dto.request.KpiCreateDTO;
import com.dao.momentum.evaluation.kpi.command.application.dto.request.KpiCreateRequest;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.CancelKpiResponse;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.KpiCreateResponse;
import com.dao.momentum.evaluation.kpi.command.domain.aggregate.Kpi;
import com.dao.momentum.evaluation.kpi.command.domain.repository.KpiRepository;
import com.dao.momentum.evaluation.kpi.exception.KpiException;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiCommandServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private KpiRepository kpiRepository;

    @InjectMocks
    private KpiCommandServiceImpl service;

    private final Long empId = 1L;
    private final Long kpiId = 100L;
    private final String reason = "목표 변경";

    private Kpi mockKpi(Status status, UseStatus isDeleted, Long ownerId) {
        return Kpi.builder()
                .kpiId(kpiId)
                .empId(ownerId)
                .statusId(status.getId())
                .isDeleted(isDeleted)
                .build();
    }

    private KpiCreateRequest createRequest() {
        KpiCreateRequest request = new KpiCreateRequest();
        setField(request, "goal", "분기 매출 10억 달성");
        setField(request, "goalValue", 1_000_000_000);
        setField(request, "kpiProgress", 0);
        setField(request, "progress25", "계약 건수 5건");
        setField(request, "progress50", "계약 건수 10건");
        setField(request, "progress75", "계약 건수 15건");
        setField(request, "progress100", "계약 건수 20건");
        setField(request, "deadline", LocalDate.of(2025, 12, 31));
        return request;
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field '" + fieldName + "'", e);
        }
    }

    @Test
    @DisplayName("KPI 생성 성공")
    void createKpi_success() {
        KpiCreateRequest request = createRequest();
        KpiCreateDTO dto = request.toDTO();
        Kpi saved = mock(Kpi.class);

        when(saved.getKpiId()).thenReturn(kpiId);
        when(kpiRepository.save(any(Kpi.class))).thenReturn(saved);

        KpiCreateResponse response = service.createKpi(empId, dto);

        assertThat(response).isNotNull();
        assertThat(response.getKpiId()).isEqualTo(kpiId);
        assertThat(response.getMessage()).contains("성공적으로 생성");
        verify(kpiRepository).save(any(Kpi.class));
    }

    @Test
    @DisplayName("KPI 취소 성공")
    void cancelKpi_success() {
        Kpi kpi = mockKpi(Status.ACCEPTED, UseStatus.N, empId);
        when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));
        when(kpiRepository.save(any(Kpi.class))).thenReturn(kpi);

        CancelKpiResponse response = service.cancelKpi(empId, kpiId, reason);

        assertThat(response).isNotNull();
        assertThat(response.getKpiId()).isEqualTo(kpiId);
        assertThat(response.getMessage()).contains("성공적으로 취소");
        verify(kpiRepository).save(kpi);
    }

    @Test
    @DisplayName("KPI가 존재하지 않으면 예외 발생")
    void cancelKpi_kpiNotFound() {
        when(kpiRepository.findById(kpiId)).thenReturn(Optional.empty());

        KpiException ex = assertThrows(KpiException.class, () ->
                service.cancelKpi(empId, kpiId, reason)
        );
        assertEquals(ErrorCode.KPI_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("본인이 아닌 KPI를 취소하려 하면 예외 발생")
    void cancelKpi_forbidden() {
        Kpi kpi = mockKpi(Status.ACCEPTED, UseStatus.N, 999L);
        when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));

        KpiException ex = assertThrows(KpiException.class, () ->
                service.cancelKpi(empId, kpiId, reason)
        );
        assertEquals(ErrorCode.KPI_REQUEST_FORBIDDEN, ex.getErrorCode());
    }

    @Test
    @DisplayName("이미 취소된 KPI는 다시 취소할 수 없음")
    void cancelKpi_alreadyDeleted() {
        Kpi kpi = mockKpi(Status.ACCEPTED, UseStatus.Y, empId);
        when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));

        KpiException ex = assertThrows(KpiException.class, () ->
                service.cancelKpi(empId, kpiId, reason)
        );
        assertEquals(ErrorCode.KPI_INVALID_STATUS, ex.getErrorCode());
    }

    @Test
    @DisplayName("승인되지 않은 KPI는 취소할 수 없음")
    void cancelKpi_notAcceptedStatus() {
        Kpi kpi = mockKpi(Status.PENDING, UseStatus.N, empId);
        when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));

        KpiException ex = assertThrows(KpiException.class, () ->
                service.cancelKpi(empId, kpiId, reason)
        );
        assertEquals(ErrorCode.KPI_INVALID_STATUS, ex.getErrorCode());
    }
}
