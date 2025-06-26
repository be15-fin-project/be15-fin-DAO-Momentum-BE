package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.evaluation.eval.query.dto.request.PeerEvaluationExcelRequestDto;

public interface ExcelEvaluationDownloadService {

    byte[] downloadPeerEvaluationExcel(PeerEvaluationExcelRequestDto request);
}
