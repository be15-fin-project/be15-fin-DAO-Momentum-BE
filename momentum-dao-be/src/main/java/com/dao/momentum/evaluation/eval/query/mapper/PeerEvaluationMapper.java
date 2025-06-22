package com.dao.momentum.evaluation.eval.query.mapper;

import com.dao.momentum.evaluation.eval.query.dto.request.PeerEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.FactorScoreDto;
import com.dao.momentum.evaluation.eval.query.dto.response.PeerEvaluationDetailResponseDto;
import com.dao.momentum.evaluation.eval.query.dto.response.PeerEvaluationResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface PeerEvaluationMapper {

    // 사원 간 평가 내역 조회
    long countPeerEvaluations(PeerEvaluationListRequestDto request);

    List<PeerEvaluationResponseDto> findPeerEvaluations(PeerEvaluationListRequestDto request);

    // 사원 간 평가 상세 조회
    PeerEvaluationDetailResponseDto findPeerEvaluationDetail(@Param("resultId") Long resultId);
    List<FactorScoreDto> findFactorScores(@Param("resultId") Long resultId);
}
