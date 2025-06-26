package com.dao.momentum.retention.prospect.command.application.service;

import com.dao.momentum.retention.prospect.command.application.dto.request.RetentionInsightDto;
import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionInsight;
import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionSupport;
import com.dao.momentum.retention.prospect.command.domain.repository.RetentionInsightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RetentionInsightCommandServiceImpl implements RetentionInsightCommandService {

    private final RetentionInsightRepository insightRepository;

    @Override
    public void saveAll(List<RetentionInsight> insights) {
        insightRepository.saveAllInsights(insights);
    }

    @Override
    public List<RetentionInsight> generateInsights(Integer roundId, List<RetentionSupport> supports) {
        // TODO: 부서-직급별로 그룹핑하고 통계 계산 (샘플은 deptId만 고려)
        Map<Integer, List<RetentionSupport>> grouped = supports.stream()
                .collect(Collectors.groupingBy(s -> getDeptIdFromEmployee(s.getEmpId()))); // deptId는 별도 조회해야 정확함

        List<RetentionInsight> result = new ArrayList<>();
        for (Map.Entry<Integer, List<RetentionSupport>> entry : grouped.entrySet()) {
            Integer deptId = entry.getKey();
            List<RetentionSupport> list = entry.getValue();

            // TODO: 평균, 분위수 등 통계 계산 필요
            RetentionInsightDto dto = new RetentionInsightDto(
                    deptId,
                    null,
                    (int) list.stream().mapToInt(RetentionSupport::getRetentionScore).average().orElse(0),
                    list.size(),
                    0, 0, 0, 0, 0 // TODO: 분위수 통계 계산
            );
            result.add(RetentionInsight.of(roundId, dto));
        }

        return result;
    }

    private Integer getDeptIdFromEmployee(Long empId) {
        // TODO: employee 테이블 또는 캐시에서 부서 ID 가져오기
        return 1;
    }
}
