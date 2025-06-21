package com.dao.momentum.evaluation.eval.query.mapper;

import com.dao.momentum.evaluation.eval.query.dto.request.PeerEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.PeerEvaluationResponseDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PeerEvaluationMapper {

    // 사원 간 평가 내역 조회
    long countPeerEvaluations(PeerEvaluationListRequestDto request);

    List<PeerEvaluationResponseDto> findPeerEvaluations(PeerEvaluationListRequestDto request);
}
