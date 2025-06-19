package com.dao.momentum.evaluation.query.service;

import com.dao.momentum.evaluation.query.dto.request.KpiListRequestDto;
import com.dao.momentum.evaluation.query.dto.response.KpiListResultDto;

import java.util.List;

public interface KpiQueryService {
    KpiListResultDto getKpiList(KpiListRequestDto requestDto);
}
