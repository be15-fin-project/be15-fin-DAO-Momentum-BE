package com.dao.momentum.retention.prospect.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionRoundRawDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionRoundSearchRequestDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionRoundListResponseDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionRoundListResultDto;
import com.dao.momentum.retention.prospect.query.mapper.RetentionRoundMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RetentionRoundQueryServiceImpl implements RetentionRoundQueryService {

    private final RetentionRoundMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public RetentionRoundListResultDto getRetentionRounds(RetentionRoundSearchRequestDto req) {
        List<RetentionRoundRawDto> rawList = mapper.findRetentionRounds(req);
        long total = mapper.countRetentionRounds(req);

        List<RetentionRoundListResponseDto> resultList = rawList.stream()
                .map(raw -> RetentionRoundListResponseDto.builder()
                        .roundId(raw.getRoundId())
                        .roundNo(raw.getRoundNo())
                        .year(raw.getYear())
                        .month(raw.getMonth())
                        .periodStart(String.valueOf(raw.getStartDate()))
                        .periodEnd(String.valueOf(raw.getEndDate()))
                        .status(determineStatus(raw.getStartDate(), raw.getEndDate()))
                        .participantCount(raw.getParticipantCount())
                        .build())
                .collect(Collectors.toList());

        Pagination pagination = Pagination.builder()
                .currentPage(req.getPage())
                .totalItems(total)
                .totalPage((int) Math.ceil((double) total / req.getSize()))
                .build();

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
