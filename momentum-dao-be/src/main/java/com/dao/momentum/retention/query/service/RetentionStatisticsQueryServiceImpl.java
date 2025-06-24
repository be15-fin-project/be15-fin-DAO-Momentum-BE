package com.dao.momentum.retention.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.retention.exception.RetentionException;
import com.dao.momentum.retention.query.dto.request.RetentionStatisticsRequestDto;
import com.dao.momentum.retention.query.dto.response.RetentionAverageScoreDto;
import com.dao.momentum.retention.query.dto.request.StabilityRatioByDeptRaw;
import com.dao.momentum.retention.query.dto.response.StabilityDistributionByDeptDto;
import com.dao.momentum.retention.query.mapper.RetentionStatisticsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RetentionStatisticsQueryServiceImpl implements RetentionStatisticsQueryService {

    private final RetentionStatisticsMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public RetentionAverageScoreDto getAverageScore(RetentionStatisticsRequestDto req) {
        RetentionAverageScoreDto raw = mapper.findAverageRetentionScore(req);

        if (raw == null || raw.getAverageScore() == null) {
            throw new RetentionException(ErrorCode.RETENTION_FORECAST_NOT_FOUND);
        }

        return RetentionAverageScoreDto.builder()
                .averageScore(raw != null ? raw.getAverageScore() : 0.0)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StabilityDistributionByDeptDto> getStabilityDistributionByDept(RetentionStatisticsRequestDto req) {

        int targetYear = (req.getYear() != null) ? req.getYear() : LocalDate.now().getYear();
        req.setYear(targetYear); // Mapper에서 사용하므로 DTO에도 세팅

        List<StabilityRatioByDeptRaw> rawList = mapper.findStabilityDistributionByDept(req);

        if (rawList == null) {
            throw new RetentionException(ErrorCode.RETENTION_FORECAST_NOT_FOUND);
        }

        return rawList.stream()
                .map(raw -> StabilityDistributionByDeptDto.builder()
                        .deptName(raw.getDeptName())
                        .stableRatio(calcRatio(raw.getStableCount(), raw.getTotalCount()))
                        .warningRatio(calcRatio(raw.getWarningCount(), raw.getTotalCount()))
                        .unstableRatio(calcRatio(raw.getUnstableCount(), raw.getTotalCount()))
                        .build())
                .collect(Collectors.toList());
    }


    private double calcRatio(Long count, Long total) {
        if (total == null || total == 0) return 0.0;
        if (count == null) return 0.0;
        return Math.round((count * 100.0 / total) * 10) / 10.0;
    }

    // 전체(또는 부서필터) 기준 근속 안정성 분포 통계
    @Override
    @Transactional(readOnly = true)
    public StabilityDistributionByDeptDto getOverallStabilityDistribution(RetentionStatisticsRequestDto req) {
        // year가 null이면 현재 연도 사용
        int targetYear = (req.getYear() != null) ? req.getYear() : LocalDate.now().getYear();
        req.setYear(targetYear); // Mapper에서 사용하므로 DTO에도 세팅

        StabilityRatioByDeptRaw raw = mapper.findOverallStabilityDistribution(req);

        if (raw == null || raw.getTotalCount() == null) {
            throw new RetentionException(ErrorCode.RETENTION_FORECAST_NOT_FOUND);
        }

        return StabilityDistributionByDeptDto.builder()
                .deptName(req.getDeptId() != null ? raw.getDeptName() : "전체")
                .stableRatio(calcRatio(raw.getStableCount(), raw.getTotalCount()))
                .warningRatio(calcRatio(raw.getWarningCount(), raw.getTotalCount()))
                .unstableRatio(calcRatio(raw.getUnstableCount(), raw.getTotalCount()))
                .build();
    }


}
