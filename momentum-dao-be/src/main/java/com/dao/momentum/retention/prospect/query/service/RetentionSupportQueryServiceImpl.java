package com.dao.momentum.retention.prospect.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.retention.prospect.exception.ProspectException;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionForecastRequestDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionSupportRaw;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionForecastItemDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionForecastResponseDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionSupportDetailDto;
import com.dao.momentum.retention.prospect.query.mapper.RetentionSupportMapper;
import com.dao.momentum.retention.prospect.command.domain.aggregate.StabilityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetentionSupportQueryServiceImpl implements RetentionSupportQueryService {

    private final RetentionSupportMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public RetentionForecastResponseDto getRetentionForecasts(RetentionForecastRequestDto req) {
        log.info(">>> getRetentionForecasts called");

        Integer roundNo = (req.getRoundNo() != null) ? req.getRoundNo() : mapper.findLatestRoundNo();

        List<RetentionSupportRaw> rawList = mapper.findRetentionForecasts(req, roundNo);
        long total = mapper.countRetentionForecasts(req, roundNo);

        if (rawList == null) {
            throw new ProspectException(ErrorCode.RETENTION_FORECAST_NOT_FOUND);
        }

        List<RetentionForecastItemDto> filtered = rawList.stream()
                .map(raw -> RetentionForecastItemDto.builder()
                        .retentionId(raw.getRetentionId())
                        .empNo(raw.getEmpNo())
                        .empName(raw.getEmpName())
                        .deptName(raw.getDeptName())
                        .positionName(raw.getPositionName())
                        .retentionGrade(convertScoreToGrade(raw.getRetentionScore()))
                        .stabilityType(convertScoreToStabilityType(raw.getRetentionScore()))
                        .summaryComment(raw.getSummaryComment())
                        .roundNo(raw.getRoundNo())
                        .build())
                .filter(dto -> req.getStabilityType() == null || dto.getStabilityType() == req.getStabilityType())
                .toList();

        Pagination pagination = Pagination.builder()
                .currentPage(req.getPage())
                .totalItems(total)
                .totalPage((int) Math.ceil((double) total / req.getSize()))
                .build();

        log.info("근속 전망 결과 조회 완료 - roundNo={}, filteredCount={}", roundNo, filtered.size());

        return RetentionForecastResponseDto.builder()
                .items(filtered)
                .pagination(pagination)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RetentionSupportDetailDto getSupportDetail(Long retentionId) {
        log.info(">>> getSupportDetail called - retentionId={}", retentionId);

        RetentionSupportDetailDto detail = mapper.findSupportDetail(retentionId);

        if (detail == null) {
            throw new ProspectException(ErrorCode.RETENTION_FORECAST_NOT_FOUND);
        }

        log.info("근속 상세 조회 완료 - empNo={}, name={}", detail.getEmpNo(), detail.getEmpName());

        return detail;
    }

    private String convertScoreToGrade(int score) {
        if (score >= 90) return "탁월";
        if (score >= 75) return "우수";
        if (score >= 60) return "보통";
        return "미흡";
    }

    private StabilityType convertScoreToStabilityType(int score) {
        if (score >= 80) return StabilityType.STABLE;
        if (score >= 60) return StabilityType.WARNING;
        return StabilityType.UNSTABLE;
    }
}
