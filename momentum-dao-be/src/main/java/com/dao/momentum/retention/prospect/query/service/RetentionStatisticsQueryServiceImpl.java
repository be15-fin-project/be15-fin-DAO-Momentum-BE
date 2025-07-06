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
        log.info("API 호출 시작 - getAverageScore, 요청 파라미터: roundId={}", req.roundId());

        RetentionAverageScoreDto raw = mapper.findAverageRetentionScore(req);
        if (raw == null) {
            log.error("평균 근속 지수 조회 실패 - 데이터 없음, 요청 파라미터: {}", req);
            throw new ProspectException(ErrorCode.RETENTION_FORECAST_NOT_FOUND);
        }

        RetentionAverageScoreDto result = RetentionAverageScoreDto.builder()
                .averageScore(raw.averageScore())
                .totalEmpCount(raw.totalEmpCount())
                .stabilitySafeRatio(raw.stabilitySafeRatio())
                .stabilityRiskRatio(raw.stabilityRiskRatio())
                .build();

        log.info("평균 근속 지수 조회 완료 - avgScore={}, empCount={}", result.averageScore(), result.totalEmpCount());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public StabilityDistributionByDeptDto getOverallStabilityDistribution(RetentionInsightRequestDto req) {
        log.info("API 호출 시작 - getOverallStabilityDistribution, 요청 파라미터: roundId={}", req.roundId());

        validateRoundId(req);

        StabilityDistributionByDeptDto result = mapper.findInsightDistribution(req);
        if (result == null) {
            log.error("전체 안정성 분포 조회 실패 - 데이터 없음, roundId={}", req.roundId());
            throw new ProspectException(ErrorCode.RETENTION_FORECAST_NOT_FOUND);
        }

        log.info("전체 안정성 분포 조회 완료 - roundId={}", req.roundId());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StabilityDistributionByDeptDto> getStabilityDistributionByDept(RetentionInsightRequestDto req) {
        log.info("API 호출 시작 - getStabilityDistributionByDept, 요청 파라미터: roundId={}", req.roundId());

        validateRoundId(req);

        List<StabilityDistributionByDeptDto> results = mapper.findInsightDistributionList(req);
        if (results == null) {
            log.error("부서별 안정성 분포 조회 실패 - 데이터 없음, roundId={}", req.roundId());
            throw new ProspectException(ErrorCode.RETENTION_FORECAST_NOT_FOUND);
        }

        log.info("부서별 안정성 분포 조회 완료 - count={}, roundId={}", results.size(), req.roundId());
        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RetentionMonthlyStatDto> getMonthlyRetentionStats(RetentionTimeseriesRequestDto req) {
        log.info("API 호출 시작 - getMonthlyRetentionStats, 요청 파라미터: year={}", req.year());

        if (req.year() == null) {
            req = RetentionTimeseriesRequestDto.builder().year(LocalDate.now().getYear()).build();
        }

        List<RetentionMonthlyStatDto> results = mapper.findMonthlyRetentionStats(req);

        log.info("시계열 통계 조회 완료 - year={}, count={}", req.year(), results.size());
        return results;
    }

    private void validateRoundId(RetentionInsightRequestDto req) {
        if (req.roundId() == null) {
            log.error("잘못된 요청 - roundId 없음, 요청 파라미터: {}", req);
            throw new ProspectException(ErrorCode.INVALID_REQUEST);
        }
    }
}
