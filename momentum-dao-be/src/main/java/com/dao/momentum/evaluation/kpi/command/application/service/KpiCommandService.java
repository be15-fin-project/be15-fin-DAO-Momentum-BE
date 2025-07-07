package com.dao.momentum.evaluation.kpi.command.application.service;

import com.dao.momentum.evaluation.kpi.command.application.dto.request.KpiCreateDTO;
import com.dao.momentum.evaluation.kpi.command.application.dto.request.KpiProgressUpdateRequest;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.CancelKpiResponse;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.KpiCreateResponse;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.KpiProgressUpdateResponse;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.KpiWithdrawResponse;

public interface KpiCommandService {

    // KPI 제출
    KpiCreateResponse createKpi(Long empId, KpiCreateDTO dto);

    // KPI 철회
    KpiWithdrawResponse withdrawKpi(Long empId, Long kpiId);

    // KPI 취소 요청
    CancelKpiResponse cancelKpi(Long empId, Long kpiId, String reason);

    // KPI 진척도 수정
    KpiProgressUpdateResponse updateProgress(Long empId, Long kpiId, KpiProgressUpdateRequest request);

}
