package com.dao.momentum.evaluation.eval.command.domain.repository;

import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalRound;

import java.util.Optional;

public interface EvalRoundRepository {
    EvalRound save(EvalRound evalRound);

    Optional<EvalRound> findById(Integer roundId);
}