package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.evaluation.eval.query.dto.request.PeerEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.PeerEvaluationListResultDto;

public interface EvaluationQueryService {
    // 사원 간 평가 내역 조회
    PeerEvaluationListResultDto getPeerEvaluations(PeerEvaluationListRequestDto requestDto);
}
