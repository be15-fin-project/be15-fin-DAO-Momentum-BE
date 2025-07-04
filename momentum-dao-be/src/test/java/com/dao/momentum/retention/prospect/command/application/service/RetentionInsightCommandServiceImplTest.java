package com.dao.momentum.retention.prospect.command.application.service;

import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionInsight;
import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionSupport;
import com.dao.momentum.retention.prospect.command.domain.repository.RetentionInsightRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RetentionInsightCommandServiceImplTest {

    @Mock
    private RetentionInsightRepository insightRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private RetentionInsightCommandServiceImpl service;

    private final int roundId = 1;

    private final Employee emp1 = Employee.builder().empId(101L).deptId(10).positionId(1).build();
    private final Employee emp2 = Employee.builder().empId(102L).deptId(10).positionId(1).build();
    private final Employee emp3 = Employee.builder().empId(103L).deptId(20).positionId(2).build();

    @Test
    @DisplayName("근속 인사이트 생성 - 부서+직위별 그룹핑 및 평균 계산 성공")
    void generateInsights_success() {
        // given
        List<RetentionSupport> supports = List.of(
                RetentionSupport.builder().empId(101L).retentionScore(70).build(),
                RetentionSupport.builder().empId(102L).retentionScore(90).build(),
                RetentionSupport.builder().empId(103L).retentionScore(50).build()
        );

        List<Employee> employees = List.of(emp1, emp2, emp3);
        when(employeeRepository.findAllById(Set.of(101L, 102L, 103L))).thenReturn(employees);

        // when
        List<RetentionInsight> result = service.generateInsights(roundId, supports);

        // then
        assertThat(result).hasSize(2);

        RetentionInsight insightA = result.stream()
                .filter(i -> i.getDeptId().equals(10) && i.getPositionId().equals(1))
                .findFirst().orElseThrow();
        assertThat(insightA.getRetentionScore()).isEqualTo(80); // (70 + 90) / 2
        assertThat(insightA.getEmpCount()).isEqualTo(2);

        RetentionInsight insightB = result.stream()
                .filter(i -> i.getDeptId().equals(20) && i.getPositionId().equals(2))
                .findFirst().orElseThrow();
        assertThat(insightB.getRetentionScore()).isEqualTo(50);
        assertThat(insightB.getEmpCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("근속 인사이트 생성 - 사원이 매핑되지 않으면 해당 RetentionSupport는 제외됨")
    void generateInsights_missingEmployee_skipsSupport() {
        // given
        List<RetentionSupport> supports = List.of(
                RetentionSupport.builder().empId(999L).retentionScore(88).build()
        );

        when(employeeRepository.findAllById(Set.of(999L))).thenReturn(Collections.emptyList());

        // when
        List<RetentionInsight> result = service.generateInsights(roundId, supports);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("근속 인사이트 생성 - 유효한 사원이 하나도 없을 경우 전체 결과 없음")
    void generateInsights_allSupportsSkipped() {
        // given
        List<RetentionSupport> supports = List.of(
                RetentionSupport.builder().empId(999L).retentionScore(80).build(),
                RetentionSupport.builder().empId(998L).retentionScore(60).build()
        );

        when(employeeRepository.findAllById(anySet())).thenReturn(Collections.emptyList());

        // when
        List<RetentionInsight> result = service.generateInsights(roundId, supports);

        // then
        assertThat(result).isEmpty();
    }
}
