package com.dao.momentum.evaluation.kpi.query.service;

import com.dao.momentum.evaluation.kpi.query.dto.request.KpiListRequestDto;

public interface KpiExcelDownloadService {
    byte[] downloadKpisAsExcel(KpiListRequestDto requestDto);
}
