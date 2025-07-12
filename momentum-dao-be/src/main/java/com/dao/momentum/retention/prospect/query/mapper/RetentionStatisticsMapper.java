package com.dao.momentum.retention.prospect.query.mapper;

import com.dao.momentum.retention.prospect.query.dto.request.RetentionInsightRequestDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionStatisticsRequestDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionTimeseriesRequestDto;
import com.dao.momentum.retention.prospect.query.dto.request.StabilityRatioByDeptRaw;
import com.dao.momentum.retention.prospect.query.dto.response.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RetentionStatisticsMapper {

    // 전체 평균 근속 지수 조회 (연도, 월, 부서 조건 적용)
    RetentionAverageScoreDto findAverageRetentionScore(@Param("req") RetentionStatisticsRequestDto req);

    // 조직 전체의 안정형/경고형/불안정형 비율 요약
    StabilityRatioSummaryDto findOverallStabilityRatio(@Param("req") RetentionInsightRequestDto req);

    // 부서+직위 기준 구간 분포
    List<StabilityRatioByDeptDto> findProgressDistributionByDept(@Param("req") RetentionInsightRequestDto req);

    // 월별 평균/표준편차 통계
    List<RetentionMonthlyStatDto> findMonthlyRetentionStats(@Param("req") RetentionTimeseriesRequestDto req);


}