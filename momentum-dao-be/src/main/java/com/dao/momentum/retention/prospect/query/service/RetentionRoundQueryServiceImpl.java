package com.dao.momentum.retention.prospect.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.retention.prospect.exception.ProspectException;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionRoundRawDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionRoundSearchRequestDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionRoundListResponseDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionRoundListResultDto;
import com.dao.momentum.retention.prospect.query.mapper.RetentionRoundMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetentionRoundQueryServiceImpl implements RetentionRoundQueryService {

    private final RetentionRoundMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public RetentionRoundListResultDto getRetentionRounds(RetentionRoundSearchRequestDto req) {
        log.info("API 호출 시작 - getRetentionRounds, 요청 파라미터: page={}, size={}", req.page(), req.size());

        // 회차 목록 조회
        List<RetentionRoundRawDto> rawList = mapper.findRetentionRounds(req);
        if (rawList == null) {
            log.error("회차 목록 조회 실패 - 데이터 없음, 요청 파라미터: {}", req);
            throw new ProspectException(ErrorCode.RETENTION_ROUND_NOT_FOUND);
        }

        long total = mapper.countRetentionRounds(req);

        // 응답 리스트 변환
        List<RetentionRoundListResponseDto> resultList = rawList.stream()
                .map(raw -> RetentionRoundListResponseDto.builder()
                        .roundId(raw.roundId())
                        .roundNo(raw.roundNo())
                        .year(raw.year())
                        .month(raw.month())
                        .periodStart(String.valueOf(raw.startDate()))
                        .periodEnd(String.valueOf(raw.endDate()))
                        .participantCount(raw.participantCount())
                        .build())
                .collect(Collectors.toList());

        // 페이징 정보 생성
        Pagination pagination = Pagination.builder()
                .currentPage(req.page())
                .totalItems(total)
                .totalPage((int) Math.ceil((double) total / req.size()))
                .build();

        log.info("회차 목록 조회 완료 - 총 개수: {}, 요청 페이지: {}", resultList.size(), req.page());

        return RetentionRoundListResultDto.builder()
                .content(resultList)
                .pagination(pagination)
                .build();
    }

    private String determineStatus(LocalDate startDate, LocalDate endDate) {
        LocalDate now = LocalDate.now();
        if (now.isBefore(startDate)) return "예정";
        if (!now.isAfter(endDate)) return "진행 중";
        return "완료";
    }
}
