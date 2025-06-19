package com.dao.momentum.evaluation.kpi.query.service;

import com.dao.momentum.evaluation.kpi.query.dto.request.KpiRequestListRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiRequestListResultDto;

public interface KpiRequestQueryService {
    KpiRequestListResultDto getKpiRequests(Long requesterEmpId, KpiRequestListRequestDto requestDto);
}
