package com.dao.momentum.retention.query.mapper;

import com.dao.momentum.retention.query.dto.request.RetentionStatisticsRequestDto;
import com.dao.momentum.retention.query.dto.response.RetentionAverageScoreDto;
import com.dao.momentum.retention.query.dto.request.StabilityRatioByDeptRaw;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RetentionStatisticsMapper {

    // 1. 전체 평균 근속 지수 조회 (연도, 월, 부서 조건 적용)
    RetentionAverageScoreDto findAverageRetentionScore(@Param("req") RetentionStatisticsRequestDto req);

    // 2. 연도별 + 부서별 근속 안정성 분포 집계
    List<StabilityRatioByDeptRaw> findStabilityDistributionByDept(@Param("year") int year);

    // 전체(또는 부서필터) 기준 근속 안정성 분포 통계
    StabilityRatioByDeptRaw findOverallStabilityDistribution(@Param("req") RetentionStatisticsRequestDto req);
}
