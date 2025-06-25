package com.dao.momentum.evaluation.eval.command.domain.repository;

import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalResponse;
import jakarta.validation.constraints.NotNull;

public interface EvalResponseRepository {
    EvalResponse save(EvalResponse response);
    
    boolean existsByRoundIdAndFormIdAndEvalIdAndTargetIdIsNull(@NotNull Integer roundId, Integer formId, Long empId);

    boolean existsByRoundIdAndFormIdAndEvalIdAndTargetId(@NotNull Integer roundId, Integer formId, Long empId, Long targetId);
}