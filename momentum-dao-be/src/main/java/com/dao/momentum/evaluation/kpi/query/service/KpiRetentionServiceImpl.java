package com.dao.momentum.evaluation.kpi.query.service;

import com.dao.momentum.evaluation.kpi.query.mapper.KpiQueryMapper;
import com.dao.momentum.evaluation.kpi.query.util.KpiPenaltyCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KpiRetentionServiceImpl implements KpiRetentionService {

    private final KpiQueryMapper kpiQueryMapper;

    @Override
    public int calculateTotalKpiPenalty(Long empId, Integer year, Integer month) {
        // [1] 기준 기간 계산 (6개월간)
        LocalDate endDate = LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth());
        LocalDate startDate = endDate.minusMonths(5).withDayOfMonth(1);
        LocalDate now = LocalDate.now();

        // [2] Mapper로 KPI 관련 통계 조회
        Map<String, Object> stats = kpiQueryMapper.getKpiPenaltyStats(empId, startDate, endDate, now);

        // [3] 통계 값 추출
        int startedCount = toInt(stats.get("startedCount"));
        int emptyMonths = toInt(stats.get("emptyMonths"));
        double avgProgress = toDouble(stats.get("avgProgress"));
        int underAchievedCount = toInt(stats.get("underAchievedCount"));

        // [4] 감점 계산
        return KpiPenaltyCalculator.calculateTotalPenalty(
                startedCount, emptyMonths, avgProgress, underAchievedCount
        );
    }

    private int toInt(Object value) {
        return value == null ? 0 : ((Number) value).intValue();
    }

    private double toDouble(Object value) {
        return value == null ? 0.0 : ((Number) value).doubleValue();
    }
}
