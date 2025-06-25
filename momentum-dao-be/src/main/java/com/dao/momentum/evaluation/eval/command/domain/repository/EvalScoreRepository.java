package com.dao.momentum.evaluation.eval.command.domain.repository;

import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalScore;

import java.util.List;

public interface EvalScoreRepository {
    List<EvalScore> saveAll(List<EvalScore> scores);
}