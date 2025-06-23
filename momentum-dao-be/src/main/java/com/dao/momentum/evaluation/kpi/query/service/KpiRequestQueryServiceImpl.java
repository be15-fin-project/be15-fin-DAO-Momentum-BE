package com.dao.momentum.evaluation.kpi.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.kpi.exception.KpiException;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiRequestListRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiRequestListResponseDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiRequestListResultDto;
import com.dao.momentum.evaluation.kpi.query.mapper.KpiRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KpiRequestQueryServiceImpl implements KpiRequestQueryService {

    private final KpiRequestMapper kpiRequestMapper;

    @Override
    @Transactional(readOnly = true)
    public KpiRequestListResultDto getKpiRequests(Long requesterEmpId, KpiRequestListRequestDto requestDto) {
        requestDto.setRequesterEmpId(requesterEmpId);

        long total = kpiRequestMapper.countKpiRequests(requestDto);
        List<KpiRequestListResponseDto> list = kpiRequestMapper.findKpiRequests(requestDto);

        if (list == null || list.isEmpty()) {
            throw new KpiException(ErrorCode.KPI_REQUEST_NOT_FOUND);
        }

        int totalPage = (int) Math.ceil((double) total / requestDto.getSize());
        Pagination pagination = Pagination.builder()
                .currentPage(requestDto.getPage())
                .totalPage(totalPage)
                .totalItems(total)
                .build();

        return new KpiRequestListResultDto(list, pagination);
    }

}
