package com.dao.momentum.retention.prospect.command.application.service;

import com.dao.momentum.organization.department.command.infrastructure.repository.JpaDepartmentRepository;
import com.dao.momentum.retention.prospect.command.application.dto.request.RetentionInsightDto;
import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionInsight;
import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionSupport;
import com.dao.momentum.retention.prospect.command.domain.repository.RetentionInsightRepository;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetentionInsightCommandServiceImpl implements RetentionInsightCommandService {

    private final RetentionInsightRepository insightRepository;
    private final JpaDepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public void saveAll(List<RetentionInsight> insights) {
        log.info(">>> saveAll called - count={}", insights.size());
        insightRepository.saveAllInsights(insights);
        log.info("인사이트 저장 완료 - savedCount={}", insights.size());
    }

    @Override
    public List<RetentionInsight> generateInsights(Integer roundId, List<RetentionSupport> supports) {
        log.info(">>> generateInsights called - roundId={}, supportCount={}", roundId, supports.size());

        Set<Long> empIds = supports.stream()
                .map(RetentionSupport::getEmpId)
                .collect(Collectors.toSet());

        Map<Long, Employee> empMap = employeeRepository.findAllById(empIds).stream()
                .collect(Collectors.toMap(Employee::getEmpId, e -> e));

        List<Integer> allScores = supports.stream()
                .map(RetentionSupport::getRetentionScore)
                .sorted(Comparator.reverseOrder())
                .toList();

        int[] thresholds = getPercentileThresholds(allScores);

        Map<String, List<RetentionSupport>> grouped = supports.stream()
                .filter(s -> empMap.containsKey(s.getEmpId()))
                .collect(Collectors.groupingBy(s -> {
                    Employee e = empMap.get(s.getEmpId());
                    return e.getDeptId() + "-" + e.getPositionId();
                }));

        List<RetentionInsight> result = new ArrayList<>();

        for (Map.Entry<String, List<RetentionSupport>> entry : grouped.entrySet()) {
            String key = entry.getKey();
            List<RetentionSupport> list = entry.getValue();
            if (list.isEmpty()) continue;

            String[] split = key.split("-");
            Integer deptId = Integer.valueOf(split[0]);
            Integer positionId = Integer.valueOf(split[1]);

            int empCount = list.size();
            int avg = (int) list.stream().mapToInt(RetentionSupport::getRetentionScore).average().orElse(0);

            int[] progressCounts = distributeScoresByThreshold(list, thresholds);

            RetentionInsightDto dto = new RetentionInsightDto(
                    deptId,
                    positionId,
                    avg,
                    empCount,
                    progressCounts[0], // 20
                    progressCounts[1], // 40
                    progressCounts[2], // 60
                    progressCounts[3], // 80
                    progressCounts[4]  // 100
            );

            result.add(RetentionInsight.of(roundId, dto));
        }

        log.info("인사이트 생성 완료 - insightCount={}, groupCount={}", result.size(), grouped.size());
        return result;
    }

    private int[] getPercentileThresholds(List<Integer> sortedDescScores) {
        int size = sortedDescScores.size();
        List<Integer> scores = new ArrayList<>(sortedDescScores);

        return new int[]{
                scores.get((int) Math.floor(0.2 * size)),
                scores.get((int) Math.floor(0.4 * size)),
                scores.get((int) Math.floor(0.6 * size)),
                scores.get((int) Math.floor(0.8 * size)),
                Integer.MIN_VALUE
        };
    }

    private int[] distributeScoresByThreshold(List<RetentionSupport> group, int[] thresholds) {
        int[] counts = new int[5];
        for (RetentionSupport support : group) {
            int score = support.getRetentionScore();
            if (score >= thresholds[0]) counts[4]++;
            else if (score >= thresholds[1]) counts[3]++;
            else if (score >= thresholds[2]) counts[2]++;
            else if (score >= thresholds[3]) counts[1]++;
            else counts[0]++;
        }
        return counts;
    }

    private int percentileThreshold(List<Integer> sorted, double percentile) {
        if (sorted.isEmpty()) return 0;
        int index = (int) Math.floor(percentile * sorted.size());
        index = Math.min(index, sorted.size() - 1);
        return sorted.get(index);
    }
}
