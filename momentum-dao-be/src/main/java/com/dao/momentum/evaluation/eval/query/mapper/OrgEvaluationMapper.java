package com.dao.momentum.evaluation.eval.query.mapper;

import com.dao.momentum.evaluation.eval.query.dto.request.OrgEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.FactorScoreDto;
import com.dao.momentum.evaluation.eval.query.dto.response.OrgEvaluationResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrgEvaluationMapper {

    // 조직 평가 목록 조회
    List<OrgEvaluationResponseDto> findOrgEvaluations(OrgEvaluationListRequestDto request);

    long countOrgEvaluations(OrgEvaluationListRequestDto request);

    // 조직 평가 세부 조회
    OrgEvaluationResponseDto findOrgEvaluationDetail(@Param("resultId") Long resultId);

    List<FactorScoreDto> findOrgFactorScores(@Param("resultId") Long resultId);
}