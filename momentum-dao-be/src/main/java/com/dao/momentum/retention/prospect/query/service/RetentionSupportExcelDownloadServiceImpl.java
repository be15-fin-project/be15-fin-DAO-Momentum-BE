package com.dao.momentum.retention.prospect.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.retention.prospect.exception.ProspectException;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionSupportExcelDto;
import com.dao.momentum.retention.prospect.query.mapper.RetentionSupportExcelMapper;
import com.dao.momentum.retention.prospect.query.util.ExcelGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RetentionSupportExcelDownloadServiceImpl implements RetentionSupportExcelDownloadService {

    private final RetentionSupportExcelMapper excelMapper;

    @Override
    public byte[] downloadExcel(Long roundId, Long deptId, String stabilityType) {
        List<RetentionSupportExcelDto> data = excelMapper.selectSupportListForExcel(roundId, deptId, stabilityType);
        if (data == null || data.isEmpty()) {
            throw new ProspectException(ErrorCode.RETENTION_FORECAST_NOT_FOUND);
        }

        return ExcelGenerator.generate(data);
    }
}
