package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import com.dao.momentum.evaluation.eval.query.dto.request.OrgEvaluationExcelRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.SelfEvaluationExcelRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.OrgEvaluationExcelDto;
import com.dao.momentum.evaluation.eval.query.dto.response.PeerEvaluationExcelDto;
import com.dao.momentum.evaluation.eval.query.dto.request.PeerEvaluationExcelRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.SelfEvaluationExcelDto;
import com.dao.momentum.evaluation.eval.query.util.OrgEvaluationExcelGenerator;
import com.dao.momentum.evaluation.eval.query.util.PeerEvaluationExcelGenerator;
import com.dao.momentum.evaluation.eval.query.mapper.ExcelEvaluationQueryMapper;
import com.dao.momentum.evaluation.eval.query.util.SelfEvaluationExcelGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelEvaluationDownloadServiceImpl implements ExcelEvaluationDownloadService {

    private final ExcelEvaluationQueryMapper excelEvaluationQueryMapper;

    @Override
    public byte[] downloadPeerEvaluationExcel(PeerEvaluationExcelRequestDto request) {
        List<PeerEvaluationExcelDto> data = excelEvaluationQueryMapper.selectPeerEvaluationsForExcel(request);

        if (data == null || data.isEmpty()) {
            throw new EvalException(ErrorCode.EVALUATION_RESULT_NOT_FOUND);
        }

        try {
            return PeerEvaluationExcelGenerator.generate(data);
        } catch (Exception e) {
            throw new EvalException(ErrorCode.EXCEL_GENERATION_FAILED);
        }
    }

    @Override
    public byte[] downloadOrgEvaluationExcel(OrgEvaluationExcelRequestDto request) {
        List<OrgEvaluationExcelDto> data = excelEvaluationQueryMapper.selectOrgEvaluationsForExcel(request);

        if (data == null ) {
            throw new EvalException(ErrorCode.EVALUATION_RESULT_NOT_FOUND);
        }

        try {
            return OrgEvaluationExcelGenerator.generate(data);
        } catch (Exception e) {
            throw new EvalException(ErrorCode.EXCEL_GENERATION_FAILED);
        }
    }

    @Override
    public byte[] downloadSelfEvaluationExcel(SelfEvaluationExcelRequestDto request) {
        List<SelfEvaluationExcelDto> data = excelEvaluationQueryMapper.selectSelfEvaluationsForExcel(request);

        if (data == null ) {
            throw new EvalException(ErrorCode.EVALUATION_RESULT_NOT_FOUND);
        }

        try {
            return SelfEvaluationExcelGenerator.generate(data);
        } catch (Exception e) {
            throw new EvalException(ErrorCode.EXCEL_GENERATION_FAILED);
        }
    }
}
