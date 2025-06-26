package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.evaluation.eval.query.dto.request.OrgEvaluationExcelRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.PeerEvaluationExcelRequestDto;

public interface ExcelEvaluationDownloadService {

    byte[] downloadPeerEvaluationExcel(PeerEvaluationExcelRequestDto request);

    byte[] downloadOrgEvaluationExcel(OrgEvaluationExcelRequestDto request);

}
