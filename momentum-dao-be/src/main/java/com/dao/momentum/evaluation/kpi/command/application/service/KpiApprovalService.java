package com.dao.momentum.evaluation.kpi.command.application.service;

import com.dao.momentum.evaluation.kpi.command.application.dto.request.KpiApprovalRequest;
import com.dao.momentum.evaluation.kpi.command.application.dto.request.KpiCancelApprovalRequest;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.KpiApprovalResponse;

public interface KpiApprovalService {

    // KPI 승인/반려 처리
    KpiApprovalResponse approveKpi(Long managerId, Long kpiId, KpiApprovalRequest request);

    // KPI 취소 승인/반려 처리
    KpiApprovalResponse approveCancelRequest(Long managerId, Long kpiId, KpiCancelApprovalRequest request);

}
