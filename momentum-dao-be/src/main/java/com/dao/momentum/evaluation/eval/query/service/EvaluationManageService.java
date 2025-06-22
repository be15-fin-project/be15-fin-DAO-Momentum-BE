package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationFormListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationRoundListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationFormResponseDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationRoundListResultDto;

import java.util.List;

public interface EvaluationManageService {

    // 평가 회차 목록 조회
    EvaluationRoundListResultDto getEvaluationRounds(EvaluationRoundListRequestDto request);

    // 평가 종류 조회
    List<EvaluationFormResponseDto> getEvaluationForms(EvaluationFormListRequestDto request);

}
