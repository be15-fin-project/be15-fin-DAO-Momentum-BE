package com.dao.momentum.evaluation.kpi.command.application.service;

import com.dao.momentum.evaluation.kpi.command.application.dto.request.KpiCreateDTO;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.KpiCreateResponse;

public interface KpiCommandService {
    KpiCreateResponse createKpi(Long empId, KpiCreateDTO dto);
}
