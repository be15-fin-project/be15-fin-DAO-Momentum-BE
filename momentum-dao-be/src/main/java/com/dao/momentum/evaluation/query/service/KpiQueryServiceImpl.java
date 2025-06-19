package com.dao.momentum.evaluation.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.exception.KpiException;
import com.dao.momentum.evaluation.query.dto.request.KpiEmployeeSummaryRequestDto;
import com.dao.momentum.evaluation.query.dto.request.KpiListRequestDto;
import com.dao.momentum.evaluation.query.dto.response.*;
import com.dao.momentum.evaluation.query.mapper.KpiQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KpiQueryServiceImpl implements KpiQueryService {

    private final KpiQueryMapper kpiQueryMapper;

    // KPI 전체 조회
    @Override
    public KpiListResultDto getKpiList(KpiListRequestDto requestDto) {
        long total = kpiQueryMapper.getKpiListCount(requestDto);
        List<KpiListResponseDto> list = kpiQueryMapper.getKpiList(requestDto);

        if (list == null || list.isEmpty()) {
            throw new KpiException(ErrorCode.KPI_LIST_NOT_FOUND);
        }

        int totalPage = (int) Math.ceil((double) total / requestDto.getSize());
        Pagination pagination = Pagination.builder()
                .currentPage(requestDto.getPage())
                .totalPage(totalPage)
                .totalItems(total)
                .build();

        return new KpiListResultDto(list, pagination);
    }

    // KPI 세부 조회
    @Override
    public KpiDetailResponseDto getKpiDetail(Long kpiId) {
        KpiDetailResponseDto detail = kpiQueryMapper.getKpiDetail(kpiId);
        if (detail == null) {
            throw new KpiException(ErrorCode.KPI_NOT_FOUND);
        }
        return detail;
    }

    // 사원별 KPI 조회
    @Override
    public KpiEmployeeSummaryResultDto getEmployeeKpiSummaries(KpiEmployeeSummaryRequestDto requestDto) {
        long total = kpiQueryMapper.countEmployeeKpiSummary(requestDto);
        List<KpiEmployeeSummaryResponseDto> list = kpiQueryMapper.getEmployeeKpiSummary(requestDto);

        if (list == null || list.isEmpty()) {
            throw new KpiException(ErrorCode.KPI_EMPLOYEE_SUMMARY_NOT_FOUND);
        }

        int totalPage = (int) Math.ceil((double) total / requestDto.getSize());
        Pagination pagination = Pagination.builder()
                .currentPage(requestDto.getPage())
                .totalPage(totalPage)
                .totalItems(total)
                .build();

        return new KpiEmployeeSummaryResultDto(list, pagination);
    }
}