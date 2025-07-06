package com.dao.momentum.evaluation.kpi.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.kpi.exception.KpiException;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiExelRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiListRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiExcelDto;
import com.dao.momentum.evaluation.kpi.query.mapper.KpiExcelMapper;
import com.dao.momentum.evaluation.kpi.query.util.KpiExcelGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KpiExcelDownloadServiceImpl implements KpiExcelDownloadService {

    private final KpiExcelMapper kpiExcelMapper;

    // KPI 목록을 Excel로 다운로드하는 메서드
    @Override
    public byte[] downloadKpisAsExcel(KpiExelRequestDto requestDto) {
        log.info("[KpiExcelDownloadServiceImpl] downloadKpisAsExcel() 호출 시작 - requestDto={}", requestDto);

        // 1) KPI 데이터를 Excel로 변환하기 위한 조회
        List<KpiExcelDto> data = kpiExcelMapper.selectKpisForExcel(requestDto);

        // 데이터가 없으면 예외 처리
        if (data == null || data.isEmpty()) {
            log.error("KPI 데이터가 없습니다 - requestDto={}", requestDto);
            throw new KpiException(ErrorCode.KPI_LIST_NOT_FOUND);
        }

        log.info("KPI 데이터 조회 완료 - data.size={}", data.size());

        try {
            // 2) Excel 파일 생성
            byte[] excelFile = KpiExcelGenerator.generate(data);
            log.info("Excel 파일 생성 완료 - data.size={}", data.size());
            return excelFile;
        } catch (Exception e) {
            // Excel 생성 실패 시 예외 처리
            log.error("Excel 파일 생성 실패 - requestDto={}, error={}", requestDto, e.getMessage());
            throw new KpiException(ErrorCode.EXCEL_GENERATION_FAILED);
        }
    }
}
