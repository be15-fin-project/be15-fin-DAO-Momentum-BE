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
        log.info("API 호출 시작 - getRetentionForecasts, 요청 파라미터: roundNo={}, stabilityType={}, page={}, size={}",
                req.roundNo(), req.stabilityType(), req.page(), req.size());

        Integer roundNo = (req.roundNo() != null) ? req.roundNo() : mapper.findLatestRoundNo();
        log.info("회차 조회 완료 - roundNo={}", roundNo);

        // 근속 전망 조회
        List<RetentionSupportRaw> rawList = mapper.findRetentionForecasts(req, roundNo);
        long total = mapper.countRetentionForecasts(req, roundNo);

        if (rawList == null || rawList.isEmpty()) {
            log.error("근속 전망 조회 실패 - 데이터 없음, 요청 파라미터: {}", req);
            throw new ProspectException(ErrorCode.RETENTION_FORECAST_NOT_FOUND);
        }

        // 필터링 후, DTO 생성
        List<RetentionForecastItemDto> filtered = rawList.stream()
                .map(raw -> RetentionForecastItemDto.builder()
                        .retentionId(raw.retentionId())
                        .empNo(raw.empNo())
                        .empName(raw.empName())
                        .deptName(raw.deptName())
                        .positionName(raw.positionName())
                        .retentionGrade(convertScoreToGrade(raw.retentionScore()))
                        .stabilityType(convertScoreToStabilityType(raw.retentionScore()))
                        .summaryComment(raw.summaryComment())
                        .roundNo(raw.roundNo())
                        .build())
                .filter(dto -> req.stabilityType() == null || dto.stabilityType() == req.stabilityType())
                .toList();

        Pagination pagination = Pagination.builder()
                .currentPage(req.page())
                .totalItems(total)
                .totalPage((int) Math.ceil((double) total / req.size()))
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
        log.info("API 호출 시작 - getSupportDetail, 요청 파라미터: retentionId={}", retentionId);

        // 근속 지원 상세 조회
        RetentionSupportDetailDto detail = mapper.findSupportDetail(retentionId);

        if (detail == null) {
            log.error("근속 상세 조회 실패 - 데이터 없음, retentionId={}", retentionId);
            throw new ProspectException(ErrorCode.RETENTION_FORECAST_NOT_FOUND);
        }

        log.info("근속 상세 조회 완료 - empNo={}, name={}", detail.empNo(), detail.empName());

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
