package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationFormListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationFormPropertyRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationRoundListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.*;

import java.util.List;

public interface EvaluationManageService {

    // 평가 회차 목록 조회
    EvaluationRoundListResultDto getEvaluationRounds(EvaluationRoundListRequestDto request);

    // 평가 종류 조회
    List<EvaluationFormResponseDto> getEvaluationForms(EvaluationFormListRequestDto request);

    // 평가 종류 트리 구조 조회
    List<EvaluationTypeTreeResponseDto> getFormTree() ;

    //  평가 양식별 요인 조회
    List<EvaluationFormPropertyDto> getFormProperties(EvaluationFormPropertyRequestDto request);

    // 평가 회차 번호 조회
    List<EvaluationRoundSimpleDto> getSimpleRoundList();

}
