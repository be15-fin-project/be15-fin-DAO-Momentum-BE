package com.dao.momentum.evaluation.kpi.query.service;

import com.dao.momentum.evaluation.kpi.query.dto.request.KpiEmployeeSummaryRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiListRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiEmployeeSummaryResultDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiListResultDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiDetailResponseDto;

public interface KpiQueryService {
    // KPI 전체 조회
    KpiListResultDto getKpiList(KpiListRequestDto requestDto);

    // KPI 세부 조회
    KpiDetailResponseDto getKpiDetail(Long kpiId);

    // 사원별 KPI 조회
    KpiEmployeeSummaryResultDto getEmployeeKpiSummaries(KpiEmployeeSummaryRequestDto requestDto);

    // 권한 반영
    KpiListResultDto getKpiListWithAccessControl(KpiListRequestDto requestDto, Long requesterEmpId, String empNo);
}
