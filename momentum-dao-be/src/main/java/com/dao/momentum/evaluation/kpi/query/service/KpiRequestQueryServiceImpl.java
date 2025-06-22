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

import java.util.List;

@Service
@RequiredArgsConstructor
public class KpiRequestQueryServiceImpl implements KpiRequestQueryService {

    private final KpiRequestMapper kpiRequestMapper;

    @Override
    public KpiRequestListResultDto getKpiRequests(Long requesterEmpId, KpiRequestListRequestDto requestDto) {
        KpiRequestListRequestDto updatedDto = KpiRequestListRequestDto.builder()
                .requesterEmpId(requesterEmpId)
                .statusId(requestDto.getStatusId())
                .completed(requestDto.getCompleted())
                .empNo(requestDto.getEmpNo())
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .page(requestDto.getPage())
                .size(requestDto.getSize())
                .build();

        long total = kpiRequestMapper.countKpiRequests(updatedDto);
        List<KpiRequestListResponseDto> list = kpiRequestMapper.findKpiRequests(updatedDto);

        if (list == null) {
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
