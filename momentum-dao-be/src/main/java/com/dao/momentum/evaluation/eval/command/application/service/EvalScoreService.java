package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalFactorScoreDto;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalScore;

import java.util.List;
import java.util.Map;

public interface EvalScoreService {

    void save(EvalScore score);

    void saveFactorScores(Long resultId, List<EvalFactorScoreDto> factorScores);

    void updateScores(Long resultId, Map<Integer, Integer> scoreMap);

    void deleteByResultId(Long resultId);

    void saveAll(List<EvalScore> scoreEntities);

    void updateOrCreateScore(Long resultId, Integer propertyId, Integer score);
}
