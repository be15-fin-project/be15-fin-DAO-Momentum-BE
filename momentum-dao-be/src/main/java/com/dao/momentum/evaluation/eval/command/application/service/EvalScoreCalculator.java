package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalSubmitRequest;

public interface EvalScoreCalculator {
    int calculateScore(Long empId, EvalSubmitRequest request);
}
