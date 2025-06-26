package com.dao.momentum.retention.prospect.command.application.service;

import com.dao.momentum.organization.department.command.domain.aggregate.Department;
import com.dao.momentum.organization.department.command.infrastructure.repository.JpaDepartmentRepository;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionInsight;
import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionSupport;
import com.dao.momentum.retention.prospect.command.domain.repository.RetentionInsightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class RetentionInsightCommandServiceImplTest {

    @Mock
    private RetentionInsightRepository insightRepository;

    @Mock
    private JpaDepartmentRepository departmentRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private RetentionInsightCommandServiceImpl service;

    private final int roundId = 1;

    private final Department deptA = Department.builder().deptId(10).build();
    private final Department deptB = Department.builder().deptId(20).build();

    private final Employee emp1 = Employee.builder().empId(101L).deptId(10).build();
    private final Employee emp2 = Employee.builder().empId(102L).deptId(10).build();
    private final Employee emp3 = Employee.builder().empId(103L).deptId(20).build();

    @Test
    @DisplayName("근속 인사이트 생성 - 부서별 그룹핑 및 평균, 퍼센타일 계산 성공")
    void generateInsights_success() {
        // given
        List<Department> depts = List.of(deptA, deptB);
        when(departmentRepository.findActiveLeafDepartments()).thenReturn(depts);

        List<RetentionSupport> supports = List.of(
                RetentionSupport.builder().empId(101L).retentionScore(70).build(),
                RetentionSupport.builder().empId(102L).retentionScore(90).build(),
                RetentionSupport.builder().empId(103L).retentionScore(50).build()
        );

        List<Employee> employees = List.of(emp1, emp2, emp3);
        when(employeeRepository.findAllById(any())).thenReturn(employees);

        // when
        List<RetentionInsight> result = service.generateInsights(roundId, supports);

        // then
        assertThat(result).hasSize(2);

        RetentionInsight insightA = result.stream()
                .filter(i -> i.getDeptId().equals(10))
                .findFirst().orElseThrow();
        assertThat(insightA.getRetentionScore()).isEqualTo(80);
        assertThat(insightA.getEmpCount()).isEqualTo(2);

        RetentionInsight insightB = result.stream()
                .filter(i -> i.getDeptId().equals(20))
                .findFirst().orElseThrow();
        assertThat(insightB.getRetentionScore()).isEqualTo(50);
        assertThat(insightB.getEmpCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("근속 인사이트 생성 - 인사이트 없는 부서는 제외됨")
    void generateInsights_emptyDepartmentIsSkipped() {
        // given
        when(departmentRepository.findActiveLeafDepartments()).thenReturn(List.of(deptA));

        List<RetentionSupport> supports = List.of(
                RetentionSupport.builder().empId(103L).retentionScore(50).build() // deptId=20
        );

        when(employeeRepository.findAllById(any()))
                .thenReturn(List.of(emp3)); // deptB 소속만 있음

        // when
        List<RetentionInsight> result = service.generateInsights(roundId, supports);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("근속 인사이트 생성 - 사원에 매핑된 부서가 없으면 제외됨")
    void generateInsights_missingEmployeeMapping_skipsSupport() {
        // given
        when(departmentRepository.findActiveLeafDepartments()).thenReturn(List.of(deptA));
        List<RetentionSupport> supports = List.of(
                RetentionSupport.builder().empId(999L).retentionScore(88).build() // unknown empId
        );
        when(employeeRepository.findAllById(Set.of(999L))).thenReturn(Collections.emptyList());

        // when
        List<RetentionInsight> result = service.generateInsights(roundId, supports);

        // then
        assertThat(result).isEmpty();
    }
}
