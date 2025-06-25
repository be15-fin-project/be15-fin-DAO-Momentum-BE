package com.dao.momentum.retention.prospect.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.retention.prospect.exception.ProspectException;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionInsightRequestDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionStatisticsRequestDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionAverageScoreDto;
import com.dao.momentum.retention.prospect.query.dto.response.StabilityDistributionByDeptDto;
import com.dao.momentum.retention.prospect.query.mapper.RetentionStatisticsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RetentionStatisticsQueryServiceImpl implements RetentionStatisticsQueryService {

    private final RetentionStatisticsMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public RetentionAverageScoreDto getAverageScore(RetentionStatisticsRequestDto req) {
        RetentionAverageScoreDto raw = mapper.findAverageRetentionScore(req);

        if (raw == null || raw.getAverageScore() == null) {
            throw new ProspectException(ErrorCode.RETENTION_FORECAST_NOT_FOUND);
        }

        return RetentionAverageScoreDto.builder()
                .averageScore(raw != null ? raw.getAverageScore() : 0.0)
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

        // 전체 조회인 경우 부서명 표시
        if (req.getDeptId() == null) {
            result = StabilityDistributionByDeptDto.builder()
                    .deptName("전체")
                    .positionName(result.getPositionName())
                    .empCount(result.getEmpCount())
                    .progress20(result.getProgress20())
                    .progress40(result.getProgress40())
                    .progress60(result.getProgress60())
                    .progress80(result.getProgress80())
                    .progress100(result.getProgress100())
                    .build();
        }

        return result;
    }

    // 부서별 근속 안정성 분포 리스트 조회
    @Override
    @Transactional(readOnly = true)
    public List<StabilityDistributionByDeptDto> getStabilityDistributionByDept(RetentionInsightRequestDto req) {
        validateRoundId(req);

        List<StabilityDistributionByDeptDto> results = mapper.findInsightDistributionList(req);

        if (results == null || results.isEmpty()) {
            throw new ProspectException(ErrorCode.RETENTION_FORECAST_NOT_FOUND);
        }

        return results;
    }

    private void validateRoundId(RetentionInsightRequestDto req) {
        if (req.getRoundId() == null) {
            throw new ProspectException(ErrorCode.INVALID_REQUEST);
        }
    }

}
