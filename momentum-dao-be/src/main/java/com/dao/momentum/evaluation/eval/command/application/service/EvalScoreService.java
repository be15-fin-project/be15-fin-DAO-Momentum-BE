package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalFactorScoreDto;

import java.util.List;

public interface EvalScoreService {
    void saveFactorScores(Long resultId, List<EvalFactorScoreDto> factorScores);
}
