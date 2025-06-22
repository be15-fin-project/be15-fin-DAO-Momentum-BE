package com.dao.momentum.evaluation.eval.query.mapper;

import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationRoundListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationRoundResponseDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EvaluationManageMapper {

    // 평가 회차 목록 조회
    List<EvaluationRoundResponseDto> findEvaluationRounds(EvaluationRoundListRequestDto request);

    long countEvaluationRounds(EvaluationRoundListRequestDto request);
}
