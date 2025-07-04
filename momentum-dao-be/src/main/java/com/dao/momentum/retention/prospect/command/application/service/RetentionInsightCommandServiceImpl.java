package com.dao.momentum.retention.prospect.command.application.service;

import com.dao.momentum.organization.department.command.domain.aggregate.Department;
import com.dao.momentum.organization.department.command.infrastructure.repository.JpaDepartmentRepository;
import com.dao.momentum.retention.prospect.command.application.dto.request.RetentionInsightDto;
import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionInsight;
import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionSupport;
import com.dao.momentum.retention.prospect.command.domain.repository.RetentionInsightRepository;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RetentionInsightCommandServiceImpl implements RetentionInsightCommandService {

    private final RetentionInsightRepository insightRepository;
    private final JpaDepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public void saveAll(List<RetentionInsight> insights) {
        insightRepository.saveAllInsights(insights);
    }

    @Override
    public List<RetentionInsight> generateInsights(Integer roundId, List<RetentionSupport> supports) {
        // 1. 모든 하위부서 없는 활성 부서 조회
        List<Department> activeLeafDepts = departmentRepository.findActiveLeafDepartments();

        // 2. empId → Employee 매핑
        Set<Long> empIds = supports.stream()
                .map(RetentionSupport::getEmpId)
                .collect(Collectors.toSet());

        List<Employee> employees = employeeRepository.findAllById(empIds);

        Map<Long, Employee> empIdToEmployeeMap = employees.stream()
                .collect(Collectors.toMap(
                        Employee::getEmpId,
                        e -> e,
                        (a, b) -> a // 중복 처리
                ));

        // 3. (deptId, positionId) → List<RetentionSupport> 그룹핑
        Map<String, List<RetentionSupport>> grouped = supports.stream()
                .filter(s -> empIdToEmployeeMap.containsKey(s.getEmpId()))
                .collect(Collectors.groupingBy(s -> {
                    Employee emp = empIdToEmployeeMap.get(s.getEmpId());
                    return emp.getDeptId() + "-" + emp.getPositionId();
                }));

        List<RetentionInsight> result = new ArrayList<>();

        // 4. 부서 + 직위 기준으로 통계 생성
        for (Department dept : activeLeafDepts) {
            // 해당 부서에 속한 직원들의 직위 목록 추출
            Set<Integer> positionIdsInDept = employees.stream()
                    .filter(e -> e.getDeptId().equals(dept.getDeptId()))
                    .map(Employee::getPositionId)
                    .collect(Collectors.toSet());

            for (Integer positionId : positionIdsInDept) {
                String key = dept.getDeptId() + "-" + positionId;
                List<RetentionSupport> list = grouped.getOrDefault(key, Collections.emptyList());
                if (list.isEmpty()) continue;

                List<Integer> scores = list.stream()
                        .map(RetentionSupport::getRetentionScore)
                        .sorted()
                        .toList();

                int empCount = scores.size();
                int avg = (int) scores.stream().mapToInt(i -> i).average().orElse(0);

                int p20 = percentileThreshold(scores, 0.2);
                int p40 = percentileThreshold(scores, 0.4);
                int p60 = percentileThreshold(scores, 0.6);
                int p80 = percentileThreshold(scores, 0.8);

                int count20 = (int) scores.stream().filter(score -> score <= p20).count();
                int count40 = (int) scores.stream().filter(score -> score > p20 && score <= p40).count();
                int count60 = (int) scores.stream().filter(score -> score > p40 && score <= p60).count();
                int count80 = (int) scores.stream().filter(score -> score > p60 && score <= p80).count();
                int count100 = empCount - count20 - count40 - count60 - count80;

                RetentionInsightDto dto = new RetentionInsightDto(
                        dept.getDeptId(),
                        positionId,
                        avg,
                        empCount,
                        count20,
                        count40,
                        count60,
                        count80,
                        count100
                );

                result.add(RetentionInsight.of(roundId, dto));
            }
        }

        return result;
    }

    private int percentileThreshold(List<Integer> sorted, double percentile) {
        if (sorted.isEmpty()) return 0;
        int index = (int) Math.floor(percentile * sorted.size());
        index = Math.min(index, sorted.size() - 1);
        return sorted.get(index);
    }
}
