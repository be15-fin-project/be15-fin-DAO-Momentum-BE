package com.dao.momentum.evaluation.eval.command.domain.repository;

import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalScore;

import java.util.List;
import java.util.Optional;

public interface EvalScoreRepository {
    List<EvalScore> saveAll(List<EvalScore> scores);

    void deleteByResultId(Long resultId);

    EvalScore save(EvalScore score);

    Optional<EvalScore> findByResultIdAndPropertyId(Long resultId, Integer propertyId);
}