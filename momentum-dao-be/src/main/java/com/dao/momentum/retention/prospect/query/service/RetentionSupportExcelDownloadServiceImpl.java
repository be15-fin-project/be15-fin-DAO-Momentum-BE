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
        log.info("API 호출 시작 - downloadExcel, 요청 파라미터: roundId={}, deptId={}, positionId={}, stabilityType={}",
                roundId, deptId, positionId, stabilityType);

        // 엑셀 데이터 조회
        List<RetentionSupportExcelDto> data = excelMapper.selectSupportListForExcel(roundId, deptId, positionId, stabilityType);
        if (data == null || data.isEmpty()) {
            log.error("엑셀 데이터 조회 실패 - 데이터 없음, roundId={}, deptId={}, positionId={}, stabilityType={}",
                    roundId, deptId, positionId, stabilityType);
            throw new ProspectException(ErrorCode.RETENTION_FORECAST_NOT_FOUND);
        }

        log.info("엑셀 데이터 조회 완료 - 데이터 건수: {}", data.size());

        // 엑셀 파일 생성
        byte[] excelFile = ExcelGenerator.generate(data);
        log.info("엑셀 파일 생성 완료 - 파일 크기: {} bytes", excelFile.length);

        return excelFile;
    }
}
