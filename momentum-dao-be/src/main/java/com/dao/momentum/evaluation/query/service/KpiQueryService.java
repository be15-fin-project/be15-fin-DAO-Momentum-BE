package com.dao.momentum.evaluation.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.query.dto.request.KpiEmployeeSummaryRequestDto;
import com.dao.momentum.evaluation.query.dto.request.KpiListRequestDto;
import com.dao.momentum.evaluation.query.dto.response.KpiEmployeeSummaryResultDto;
import com.dao.momentum.evaluation.query.dto.response.KpiListResultDto;
import com.dao.momentum.evaluation.query.dto.response.KpiDetailResponseDto;
import com.dao.momentum.evaluation.query.dto.response.KpiEmployeeSummaryResponseDto;

import java.util.List;

public interface KpiQueryService {
    // KPI 전체 조회
    KpiListResultDto getKpiList(KpiListRequestDto requestDto);

    // KPI 세부 조회
    KpiDetailResponseDto getKpiDetail(Long kpiId);

    // 사원별 KPI 조회
    KpiEmployeeSummaryResultDto getEmployeeKpiSummaries(KpiEmployeeSummaryRequestDto requestDto);

}
