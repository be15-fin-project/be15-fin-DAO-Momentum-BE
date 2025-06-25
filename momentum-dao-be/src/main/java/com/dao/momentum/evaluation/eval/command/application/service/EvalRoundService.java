package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalRoundCreateDTO;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalRoundUpdateDTO;
import com.dao.momentum.evaluation.eval.command.application.dto.response.EvalRoundUpdateResponse;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalRound;

public interface EvalRoundService {

    // 평가 회차 작성
    EvalRound create(EvalRoundCreateDTO dto);

    // 평가 회차 수정
    EvalRoundUpdateResponse update(Integer roundId, EvalRoundUpdateDTO dto);

    // 평가 회차 삭제
    void delete(Integer roundId);
}
