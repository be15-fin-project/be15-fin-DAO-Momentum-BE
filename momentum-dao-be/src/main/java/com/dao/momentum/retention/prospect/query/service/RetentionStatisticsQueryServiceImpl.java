package com.dao.momentum.retention.prospect.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.retention.prospect.exception.ProspectException;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionInsightRequestDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionStatisticsRequestDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionTimeseriesRequestDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionAverageScoreDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionMonthlyStatDto;
import com.dao.momentum.retention.prospect.query.dto.response.StabilityDistributionByDeptDto;
import com.dao.momentum.retention.prospect.query.mapper.RetentionStatisticsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetentionStatisticsQueryServiceImpl implements RetentionStatisticsQueryService {

    private final RetentionStatisticsMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public RetentionAverageScoreDto getAverageScore(RetentionStatisticsRequestDto req) {
        log.info(">>> getAverageScore called");

        RetentionAverageScoreDto raw = mapper.findAverageRetentionScore(req);
        if (raw == null) {
            throw new ProspectException(ErrorCode.RETENTION_FORECAST_NOT_FOUND);
        }

        RetentionAverageScoreDto result = RetentionAverageScoreDto.builder()
                .averageScore(raw.getAverageScore())
                .totalEmpCount(raw.getTotalEmpCount())
                .stabilitySafeRatio(raw.getStabilitySafeRatio())
                .stabilityRiskRatio(raw.getStabilityRiskRatio())
                .build();

        log.info("평균 근속 지수 조회 완료 - avgScore={}, empCount={}", result.getAverageScore(), result.getTotalEmpCount());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public StabilityDistributionByDeptDto getOverallStabilityDistribution(RetentionInsightRequestDto req) {
        log.info(">>> getOverallStabilityDistribution called");

        validateRoundId(req);

        StabilityDistributionByDeptDto result = mapper.findInsightDistribution(req);
        if (result == null) {
            throw new ProspectException(ErrorCode.RETENTION_FORECAST_NOT_FOUND);
        }

        log.info("전체 안정성 분포 조회 완료 - roundId={}", req.getRoundId());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StabilityDistributionByDeptDto> getStabilityDistributionByDept(RetentionInsightRequestDto req) {
        log.info(">>> getStabilityDistributionByDept called");

        validateRoundId(req);

        List<StabilityDistributionByDeptDto> results = mapper.findInsightDistributionList(req);
        if (results == null) {
            throw new ProspectException(ErrorCode.RETENTION_FORECAST_NOT_FOUND);
        }

        log.info("부서별 안정성 분포 조회 완료 - count={}, roundId={}", results.size(), req.getRoundId());
        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RetentionMonthlyStatDto> getMonthlyRetentionStats(RetentionTimeseriesRequestDto req) {
        log.info(">>> getMonthlyRetentionStats called");

        if (req.getYear() == null) {
            req.setYear(LocalDate.now().getYear());
        }

        List<RetentionMonthlyStatDto> results = mapper.findMonthlyRetentionStats(req);

        log.info("시계열 통계 조회 완료 - year={}, count={}", req.getYear(), results.size());
        return results;
    }

    private void validateRoundId(RetentionInsightRequestDto req) {
        if (req.getRoundId() == null) {
            throw new ProspectException(ErrorCode.INVALID_REQUEST);
        }
    }
}
