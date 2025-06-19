package com.dao.momentum.evaluation.query.service;

import com.dao.momentum.evaluation.query.dto.request.KpiListRequestDto;
import com.dao.momentum.evaluation.query.dto.response.KpiListResultDto;
import com.dao.momentum.evaluation.query.dto.response.KpiDetailResponseDto;

import java.util.List;

public interface KpiQueryService {
    // KPI 전체 조회
    KpiListResultDto getKpiList(KpiListRequestDto requestDto);

    // KPI 세부 조회
    KpiDetailResponseDto getKpiDetail(Long kpiId);
}
