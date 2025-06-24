package com.dao.momentum.evaluation.kpi.command.application.service;

import com.dao.momentum.evaluation.kpi.command.application.dto.request.KpiCreateDTO;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.CancelKpiResponse;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.KpiCreateResponse;

public interface KpiCommandService {

    // KPI 제출
    KpiCreateResponse createKpi(Long empId, KpiCreateDTO dto);

    // KPI 취소 요청
    CancelKpiResponse cancelKpi(Long empId, Long kpiId, String reason);

}
