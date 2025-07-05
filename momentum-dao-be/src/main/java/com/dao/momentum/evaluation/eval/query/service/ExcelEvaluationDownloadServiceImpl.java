package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import com.dao.momentum.evaluation.eval.query.dto.request.various.OrgEvaluationExcelRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.various.SelfEvaluationExcelRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.various.OrgEvaluationExcelDto;
import com.dao.momentum.evaluation.eval.query.dto.response.various.PeerEvaluationExcelDto;
import com.dao.momentum.evaluation.eval.query.dto.request.various.PeerEvaluationExcelRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.various.SelfEvaluationExcelDto;
import com.dao.momentum.evaluation.eval.query.util.OrgEvaluationExcelGenerator;
import com.dao.momentum.evaluation.eval.query.util.PeerEvaluationExcelGenerator;
import com.dao.momentum.evaluation.eval.query.mapper.ExcelEvaluationQueryMapper;
import com.dao.momentum.evaluation.eval.query.util.SelfEvaluationExcelGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelEvaluationDownloadServiceImpl implements ExcelEvaluationDownloadService {

    private final ExcelEvaluationQueryMapper excelEvaluationQueryMapper;

    @Override
    public byte[] downloadPeerEvaluationExcel(PeerEvaluationExcelRequestDto request) {
        log.info("[ExcelEvaluationDownloadServiceImpl] downloadPeerEvaluationExcel() 호출 시작 - filter={}", request);

        List<PeerEvaluationExcelDto> data = excelEvaluationQueryMapper.selectPeerEvaluationsForExcel(request);

        if (data == null || data.isEmpty()) {
            log.error("Peer 평가 데이터 없음 - filter={}", request);
            throw new EvalException(ErrorCode.EVALUATION_RESULT_NOT_FOUND);
        }

        log.info("Peer 평가 엑셀 조회 결과 - row count={}", data.size());

        try {
            byte[] excelData = PeerEvaluationExcelGenerator.generate(data);
            log.info("Peer 평가 엑셀 생성 완료");
            return excelData;
        } catch (Exception e) {
            log.warn("Peer 평가 엑셀 생성 실패 - filter={}", request, e);
            throw new EvalException(ErrorCode.EXCEL_GENERATION_FAILED);
        }
    }

    @Override
    public byte[] downloadOrgEvaluationExcel(OrgEvaluationExcelRequestDto request) {
        log.info("[ExcelEvaluationDownloadServiceImpl] downloadOrgEvaluationExcel() 호출 시작 - filter={}", request);

        List<OrgEvaluationExcelDto> data = excelEvaluationQueryMapper.selectOrgEvaluationsForExcel(request);

        if (data == null || data.isEmpty()) {
            log.error("Org 평가 데이터 없음 - filter={}", request);
            throw new EvalException(ErrorCode.EVALUATION_RESULT_NOT_FOUND);
        }

        log.info("Org 평가 엑셀 조회 결과 - row count={}", data.size());

        try {
            byte[] excelData = OrgEvaluationExcelGenerator.generate(data);
            log.info("Org 평가 엑셀 생성 완료");
            return excelData;
        } catch (Exception e) {
            log.warn("Org 평가 엑셀 생성 실패 - filter={}", request, e);
            throw new EvalException(ErrorCode.EXCEL_GENERATION_FAILED);
        }
    }

    @Override
    public byte[] downloadSelfEvaluationExcel(SelfEvaluationExcelRequestDto request) {
        log.info("[ExcelEvaluationDownloadServiceImpl] downloadSelfEvaluationExcel() 호출 시작 - filter={}", request);

        List<SelfEvaluationExcelDto> data = excelEvaluationQueryMapper.selectSelfEvaluationsForExcel(request);

        if (data == null || data.isEmpty()) {
            log.error("Self 평가 데이터 없음 - filter={}", request);
            throw new EvalException(ErrorCode.EVALUATION_RESULT_NOT_FOUND);
        }

        log.info("Self 평가 엑셀 조회 결과 - row count={}", data.size());

        try {
            byte[] excelData = SelfEvaluationExcelGenerator.generate(data);
            log.info("Self 평가 엑셀 생성 완료");
            return excelData;
        } catch (Exception e) {
            log.warn("Self 평가 엑셀 생성 실패 - filter={}", request, e);
            throw new EvalException(ErrorCode.EXCEL_GENERATION_FAILED);
        }
    }
}
