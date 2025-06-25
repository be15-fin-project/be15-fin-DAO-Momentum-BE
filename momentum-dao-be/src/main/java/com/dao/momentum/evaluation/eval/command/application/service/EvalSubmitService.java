package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalSubmitRequest;
import com.dao.momentum.evaluation.eval.command.application.dto.response.EvalSubmitResponse;

public interface EvalSubmitService {
    EvalSubmitResponse submitEvaluation(Long empId, EvalSubmitRequest request);
}
