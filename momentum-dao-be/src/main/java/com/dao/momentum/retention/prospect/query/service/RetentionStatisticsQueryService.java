package com.dao.momentum.retention.prospect.query.service;

import com.dao.momentum.retention.prospect.query.dto.request.RetentionInsightRequestDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionStatisticsRequestDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionTimeseriesRequestDto;
import com.dao.momentum.retention.prospect.query.dto.request.StabilityRatioByDeptRaw;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionAverageScoreDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionMonthlyStatDto;
import com.dao.momentum.retention.prospect.query.dto.response.StabilityRatioByDeptDto;
import com.dao.momentum.retention.prospect.query.dto.response.StabilityRatioSummaryDto;

import java.util.List;

public interface RetentionStatisticsQueryService {

    /**
     * 전체 평균 근속 지수 및 안정성 비율 조회
     */
    RetentionAverageScoreDto getAverageScore(RetentionStatisticsRequestDto req);

    /**
     * 전체 조직의 안정성 유형 분포 요약 (안정형/경고형/불안정형)
     */
    StabilityRatioSummaryDto getOverallStabilityDistribution(RetentionInsightRequestDto req);

    /**
     * 부서+직위별 근속 안정성 분포 통계
     */
    List<StabilityRatioByDeptDto> getStabilityDistributionByDept(RetentionInsightRequestDto req);

    /**
     * 월별 평균 근속 지수 및 표준편차 시계열 조회
     */
    List<RetentionMonthlyStatDto> getMonthlyRetentionStats(RetentionTimeseriesRequestDto req);
}
