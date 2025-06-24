package com.dao.momentum.evaluation.eval.command.domain.repository;

import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalRound;

public interface EvalRoundRepository {
    EvalRound save(EvalRound evalRound);
}