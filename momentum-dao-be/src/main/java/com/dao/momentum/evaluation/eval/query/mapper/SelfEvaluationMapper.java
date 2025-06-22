package com.dao.momentum.evaluation.eval.query.mapper;

import com.dao.momentum.evaluation.eval.query.dto.request.SelfEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.SelfEvaluationResponseDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SelfEvaluationMapper {

    // 자가 진단 내역 조회
    long countSelfEvaluations(SelfEvaluationListRequestDto requestDto);

    List<SelfEvaluationResponseDto> findSelfEvaluations(SelfEvaluationListRequestDto requestDto);
}
