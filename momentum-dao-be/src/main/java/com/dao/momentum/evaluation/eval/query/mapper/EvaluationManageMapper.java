package com.dao.momentum.evaluation.eval.query.mapper;

import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationFormListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationRoundListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface EvaluationManageMapper {

    // 평가 회차 목록 조회
    List<EvaluationRoundResponseDto> findEvaluationRounds(EvaluationRoundListRequestDto request);

    long countEvaluationRounds(EvaluationRoundListRequestDto request);

    // 평가 종류 조회
    List<EvaluationFormResponseDto> findEvaluationForms(EvaluationFormListRequestDto request);

    // 평가 타입 목록 조회
    List<EvaluationTypeDto> findAllEvalTypes();

    // 활성화된 평가 양식 목록 조회
    List<EvaluationFormDto> findAllActiveForms();

    // 평가 종류별 요인 조회
    List<EvaluationFormPropertyDto> findFormProperties(@Param("formId") Long formId);

    // 평가 회차 번호 조회
    List<EvaluationRoundSimpleDto> findSimpleRounds();

}
