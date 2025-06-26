package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalSubmitRequest;
import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrWeight;

import java.util.Map;

public interface EvalScoreCalculator {
    int calculateScore(Long empId, EvalSubmitRequest request);

    int calculateOverallScore(Map<Integer, Integer> scoreMap, HrWeight weight);

}
