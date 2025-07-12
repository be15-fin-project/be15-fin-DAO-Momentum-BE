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
        log.info("API 호출 시작 - saveAll, 요청 파라미터: insightsCount={}", insights.size());

        insightRepository.saveAllInsights(insights);

        log.info("API 호출 성공 - saveAll, 저장 완료 - savedCount={}", insights.size());
    }

    @Override
    public List<RetentionInsight> generateInsights(Integer roundId, List<RetentionSupport> supports) {
        log.info("API 호출 시작 - generateInsights, 요청 파라미터: roundId={}, supportCount={}", roundId, supports.size());

        // EmpId 추출 및 매핑
        Set<Long> empIds = supports.stream()
                .map(RetentionSupport::getEmpId)
                .collect(Collectors.toSet());

        Map<Long, Employee> empMap = employeeRepository.findAllById(empIds).stream()
                .collect(Collectors.toMap(Employee::getEmpId, e -> e));

        // 모든 점수 정렬 및 백분위수 임계값 계산
        List<Integer> allScores = supports.stream()
                .map(s -> s.getRetentionScore().intValue())
                .sorted(Comparator.reverseOrder())
                .toList();


        int[] thresholds = getPercentileThresholds(allScores);

        // 그룹화된 데이터 처리
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
            int avg = (int) list.stream()
                    .mapToInt(s -> s.getRetentionScore().intValue())
                    .average()
                    .orElse(0);

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

        log.info("API 호출 성공 - generateInsights, 인사이트 생성 완료 - insightCount={}, groupCount={}", result.size(), grouped.size());
        return result;
    }

    private int[] getPercentileThresholds(List<Integer> sortedDescScores) {
        int size = sortedDescScores.size();
        List<Integer> scores = new ArrayList<>(sortedDescScores);

        int[] thresholds = new int[5];
        thresholds[0] = percentileThreshold(scores, 0.2);
        thresholds[1] = percentileThreshold(scores, 0.4);
        thresholds[2] = percentileThreshold(scores, 0.6);
        thresholds[3] = percentileThreshold(scores, 0.8);
        thresholds[4] = Integer.MIN_VALUE;

        log.info("백분위수 임계값 계산 완료 - 20th: {}, 40th: {}, 60th: {}, 80th: {}", thresholds[0], thresholds[1], thresholds[2], thresholds[3]);
        return thresholds;
    }

    private int[] distributeScoresByThreshold(List<RetentionSupport> group, int[] thresholds) {
        int[] counts = new int[5];
        for (RetentionSupport support : group) {
            int score = support.getRetentionScore().intValue();
            if (score >= thresholds[0]) counts[4]++;
            else if (score >= thresholds[1]) counts[3]++;
            else if (score >= thresholds[2]) counts[2]++;
            else if (score >= thresholds[3]) counts[1]++;
            else counts[0]++;
        }

        log.info("점수 분포 완료 - 20: {}, 40: {}, 60: {}, 80: {}, 100: {}", counts[0], counts[1], counts[2], counts[3], counts[4]);
        return counts;
    }

    private int percentileThreshold(List<Integer> sorted, double percentile) {
        if (sorted.isEmpty()) return 0;
        int index = (int) Math.floor(percentile * sorted.size());
        index = Math.min(index, sorted.size() - 1);
        return sorted.get(index);
    }
}
