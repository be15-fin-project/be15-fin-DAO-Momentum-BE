package com.dao.momentum.evaluation.kpi.query.service;

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
    public byte[] downloadKpisAsExcel(KpiListRequestDto requestDto) {
        List<KpiExcelDto> data = kpiExcelMapper.selectKpisForExcel(requestDto);
        return KpiExcelGenerator.generate(data);
    }
}
