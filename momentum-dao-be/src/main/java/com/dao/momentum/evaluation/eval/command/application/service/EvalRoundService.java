package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalRoundCreateDTO;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalRound;

public interface EvalRoundService {
    EvalRound create(EvalRoundCreateDTO dto);
}
