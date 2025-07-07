package com.dao.momentum.retention.prospect.query.mapper;

import com.dao.momentum.retention.prospect.query.dto.request.RetentionInsightRequestDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionStatisticsRequestDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionTimeseriesRequestDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionAverageScoreDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionMonthlyStatDto;
import com.dao.momentum.retention.prospect.query.dto.response.StabilityDistributionByDeptDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RetentionStatisticsMapper {

    // 전체 평균 근속 지수 조회 (연도, 월, 부서 조건 적용)
    RetentionAverageScoreDto findAverageRetentionScore(@Param("req") RetentionStatisticsRequestDto req);

    // 단일 부서 또는 전체 기준 근속 안정성 분포 조회
    StabilityDistributionByDeptDto findInsightDistribution(@Param("req") RetentionInsightRequestDto req);

    // 부서별 근속 안정성 분포 리스트 조회 (roundId 필수)
    List<StabilityDistributionByDeptDto> findInsightDistributionList(@Param("req") RetentionInsightRequestDto req);

    // 평균 및 표준편차 시계열 조회
    List<RetentionMonthlyStatDto> findMonthlyRetentionStats(@Param("req") RetentionTimeseriesRequestDto req);


}