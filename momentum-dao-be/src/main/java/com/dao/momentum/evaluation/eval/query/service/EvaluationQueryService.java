package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.evaluation.eval.query.dto.request.OrgEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.PeerEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.OrgEvaluationDetailResultDto;
import com.dao.momentum.evaluation.eval.query.dto.response.OrgEvaluationListResultDto;
import com.dao.momentum.evaluation.eval.query.dto.response.PeerEvaluationDetailResultDto;
import com.dao.momentum.evaluation.eval.query.dto.response.PeerEvaluationListResultDto;

public interface EvaluationQueryService {
    // 사원 간 평가 내역 조회
    PeerEvaluationListResultDto getPeerEvaluations(PeerEvaluationListRequestDto requestDto);

    // 사원 간 평가 상세 조회
    PeerEvaluationDetailResultDto getPeerEvaluationDetail(Long resultId);

    // 조직 평가 내역 조회
    OrgEvaluationListResultDto getOrgEvaluations(OrgEvaluationListRequestDto requestDto);

    // 조직 평가 상세 조회
    OrgEvaluationDetailResultDto getOrgEvaluationDetail(Long resultId);

}