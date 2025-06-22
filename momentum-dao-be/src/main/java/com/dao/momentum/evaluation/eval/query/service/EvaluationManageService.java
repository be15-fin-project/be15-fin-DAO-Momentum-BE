package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationRoundListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationRoundListResultDto;

public interface EvaluationManageService {

    // 평가 회차 목록 조회
    EvaluationRoundListResultDto getEvaluationRounds(EvaluationRoundListRequestDto request);
}
