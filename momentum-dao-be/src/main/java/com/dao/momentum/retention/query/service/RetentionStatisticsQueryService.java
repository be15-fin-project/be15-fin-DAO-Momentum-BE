package com.dao.momentum.retention.query.service;

import com.dao.momentum.retention.query.dto.request.RetentionStatisticsRequestDto;
import com.dao.momentum.retention.query.dto.response.RetentionAverageScoreDto;
import com.dao.momentum.retention.query.dto.response.StabilityDistributionByDeptDto;

import java.util.List;

public interface RetentionStatisticsQueryService {

    // 전체 평균 근속 지수 조회
    RetentionAverageScoreDto getAverageScore(RetentionStatisticsRequestDto req);

    // 연도별 부서별 근속 안정성 유형 분포 조회
    List<StabilityDistributionByDeptDto> getStabilityDistributionByDept(RetentionStatisticsRequestDto req);

    // 전체(또는 부서필터) 기준 근속 안정성 분포 통계
    StabilityDistributionByDeptDto getOverallStabilityDistribution(RetentionStatisticsRequestDto req);

}
