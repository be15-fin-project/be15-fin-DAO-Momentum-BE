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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RetentionSupportQueryServiceImpl implements RetentionSupportQueryService {

    private final RetentionSupportMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public RetentionForecastResponseDto getRetentionForecasts(RetentionForecastRequestDto req) {

        // 1. 회차 번호 기본 처리
        Integer roundNo = (req.getRoundNo() != null) ? req.getRoundNo() : mapper.findLatestRoundNo();

        // 2. 조회
        List<RetentionSupportRaw> rawList = mapper.findRetentionForecasts(req, roundNo);
        long total = mapper.countRetentionForecasts(req, roundNo);

        if (rawList == null) {
            throw new ProspectException(ErrorCode.RETENTION_FORECAST_NOT_FOUND);
        }

        // 3. 안정성 유형 필터링 (Java에서 처리)
        List<RetentionForecastItemDto> filtered =
                rawList.stream()
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

        // 4. 페이지네이션 정보 생성
        Pagination pagination = Pagination.builder()
                .currentPage(req.getPage())
                .totalItems(total)
                .totalPage((int) Math.ceil((double) total / req.getSize()))
                .build();

        // 5. 결과 조립
        return RetentionForecastResponseDto.builder()
                .items(filtered)
                .pagination(pagination)
                .build();
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

    @Override
    @Transactional(readOnly = true)
    public RetentionSupportDetailDto getSupportDetail(Long retentionId) {
        RetentionSupportDetailDto detail = mapper.findSupportDetail(retentionId);

        if (detail == null) {
            throw new ProspectException(ErrorCode.RETENTION_FORECAST_NOT_FOUND);
        }

        return detail;
    }

}
