package com.dao.momentum.evaluation.kpi.query.service;

import com.dao.momentum.evaluation.kpi.query.dto.request.KpiExelRequestDto;

public interface KpiExcelDownloadService {
    byte[] downloadKpisAsExcel(KpiExelRequestDto requestDto);
}
