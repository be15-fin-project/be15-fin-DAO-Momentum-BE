package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import com.dao.momentum.evaluation.eval.query.dto.response.PeerEvaluationExcelDto;
import com.dao.momentum.evaluation.eval.query.dto.request.PeerEvaluationExcelRequestDto;
import com.dao.momentum.evaluation.eval.query.util.PeerEvaluationExcelGenerator;
import com.dao.momentum.evaluation.eval.query.mapper.ExcelEvaluationQueryMapper;
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

}
