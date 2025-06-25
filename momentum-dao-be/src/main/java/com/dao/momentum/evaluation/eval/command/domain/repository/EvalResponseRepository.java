package com.dao.momentum.evaluation.eval.command.domain.repository;

import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalResponse;

public interface EvalResponseRepository {
    EvalResponse save(EvalResponse response);
}