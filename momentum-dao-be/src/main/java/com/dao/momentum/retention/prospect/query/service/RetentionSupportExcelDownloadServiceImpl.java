package com.dao.momentum.retention.prospect.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.retention.prospect.exception.ProspectException;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionSupportExcelDto;
import com.dao.momentum.retention.prospect.query.mapper.RetentionSupportExcelMapper;
import com.dao.momentum.retention.prospect.query.util.ExcelGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetentionSupportExcelDownloadServiceImpl implements RetentionSupportExcelDownloadService {

    private final RetentionSupportExcelMapper excelMapper;

    @Override
    public byte[] downloadExcel(Long roundId, Long deptId, Long positionId, String stabilityType) {
        log.info(">>> downloadExcel called - roundId={}, deptId={}, positionId={}, stabilityType={}",
                roundId, deptId, positionId, stabilityType);

        List<RetentionSupportExcelDto> data = excelMapper.selectSupportListForExcel(roundId, deptId, positionId, stabilityType);
        if (data == null || data.isEmpty()) {
            throw new ProspectException(ErrorCode.RETENTION_FORECAST_NOT_FOUND);
        }

        log.info("엑셀 데이터 건수: {}", data.size());
        return ExcelGenerator.generate(data);
    }
}
