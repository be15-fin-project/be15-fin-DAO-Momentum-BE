package com.dao.momentum.evaluation.kpi.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.kpi.command.application.dto.request.KpiCreateDTO;
import com.dao.momentum.evaluation.kpi.command.application.dto.request.KpiCreateRequest;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.KpiCreateResponse;
import com.dao.momentum.evaluation.kpi.command.domain.aggregate.Kpi;
import com.dao.momentum.evaluation.kpi.command.domain.repository.KpiRepository;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.organization.employee.exception.EmployeeException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        // given
        KpiCreateRequest request = createRequest(); // 유효한 request 생성 메서드 사용
        KpiCreateDTO dto = request.toDTO();
        Long empId = 1L;

        Kpi saved = mock(Kpi.class);
        when(saved.getKpiId()).thenReturn(100L);
        when(kpiRepository.save(any(Kpi.class))).thenReturn(saved);

        // when
        KpiCreateResponse response = service.createKpi(empId, dto);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getKpiId()).isEqualTo(100L);
        assertThat(response.getMessage()).contains("성공적으로 생성");
        verify(kpiRepository).save(any(Kpi.class));
    }

}
