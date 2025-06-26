package com.dao.momentum.evaluation.kpi.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.kpi.exception.KpiException;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiExelRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiListRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiExcelDto;
import com.dao.momentum.evaluation.kpi.query.mapper.KpiExcelMapper;
import com.dao.momentum.evaluation.kpi.query.util.KpiExcelGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KpiExcelDownloadServiceImpl implements KpiExcelDownloadService {

    private final KpiExcelMapper kpiExcelMapper;

    @Override
    public byte[] downloadKpisAsExcel(KpiExelRequestDto requestDto) {
        List<KpiExcelDto> data = kpiExcelMapper.selectKpisForExcel(requestDto);

        if (data == null || data.isEmpty()) {
            throw new KpiException(ErrorCode.KPI_LIST_NOT_FOUND);
        }

        try {
            return KpiExcelGenerator.generate(data);
        } catch (Exception e) {
            throw new KpiException(ErrorCode.EXCEL_GENERATION_FAILED);
        }
    }
}
