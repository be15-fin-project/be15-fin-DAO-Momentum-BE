package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalSubmitRequest;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalResponse;

public interface EvalResponseService {
    EvalResponse saveResponse(Long empId, EvalSubmitRequest request, int finalScore);

    void updateFinalScoreAndReason(Long resultId, int score, String reason);

    Integer getRoundIdByResultId(Long resultId);

}
