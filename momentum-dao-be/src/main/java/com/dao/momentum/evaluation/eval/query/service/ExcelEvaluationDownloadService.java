package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.evaluation.eval.query.dto.request.various.OrgEvaluationExcelRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.various.PeerEvaluationExcelRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.various.SelfEvaluationExcelRequestDto;

public interface ExcelEvaluationDownloadService {

    byte[] downloadPeerEvaluationExcel(PeerEvaluationExcelRequestDto request);

    byte[] downloadOrgEvaluationExcel(OrgEvaluationExcelRequestDto request);

    byte[] downloadSelfEvaluationExcel(SelfEvaluationExcelRequestDto request);

}
