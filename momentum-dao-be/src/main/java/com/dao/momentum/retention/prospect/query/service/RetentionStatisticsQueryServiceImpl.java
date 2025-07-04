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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RetentionStatisticsQueryServiceImpl implements RetentionStatisticsQueryService {

    private final RetentionStatisticsMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public RetentionAverageScoreDto getAverageScore(RetentionStatisticsRequestDto req) {
        RetentionAverageScoreDto raw = mapper.findAverageRetentionScore(req);

        if (raw == null) {
            throw new ProspectException(ErrorCode.RETENTION_FORECAST_NOT_FOUND);
        }

        return RetentionAverageScoreDto.builder()
                .averageScore(raw.getAverageScore())
                .totalEmpCount(raw.getTotalEmpCount())
                .stabilitySafeRatio(raw.getStabilitySafeRatio())
                .stabilityRiskRatio(raw.getStabilityRiskRatio())
                .build();

    }

    // 전체 기준 또는 단일 부서/직급 기준 근속 안정성 분포 조회
    @Override
    @Transactional(readOnly = true)
    public StabilityDistributionByDeptDto getOverallStabilityDistribution(RetentionInsightRequestDto req) {
        validateRoundId(req);

        StabilityDistributionByDeptDto result = mapper.findInsightDistribution(req);

        if (result == null) {
            throw new ProspectException(ErrorCode.RETENTION_FORECAST_NOT_FOUND);
        }

        return result;
    }

    // 부서별 근속 안정성 분포 리스트 조회
    @Override
    @Transactional(readOnly = true)
    public List<StabilityDistributionByDeptDto> getStabilityDistributionByDept(RetentionInsightRequestDto req) {
        validateRoundId(req);

        List<StabilityDistributionByDeptDto> results = mapper.findInsightDistributionList(req);

        if (results == null) {
            throw new ProspectException(ErrorCode.RETENTION_FORECAST_NOT_FOUND);
        }

        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RetentionMonthlyStatDto> getMonthlyRetentionStats(RetentionTimeseriesRequestDto req) {
        if (req.getYear() == null) {
            req.setYear(LocalDate.now().getYear());
        }

        return mapper.findMonthlyRetentionStats(req);
    }

    private void validateRoundId(RetentionInsightRequestDto req) {
        if (req.getRoundId() == null) {
            throw new ProspectException(ErrorCode.INVALID_REQUEST);
        }
    }

}
