package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.evaluation.eval.query.dto.request.various.OrgEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.various.PeerEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.various.SelfEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.various.*;

public interface EvaluationQueryService {
    // 사원 간 평가 내역 조회
    PeerEvaluationListResultDto getPeerEvaluations(PeerEvaluationListRequestDto requestDto);

    // 사원 간 평가 상세 조회
    PeerEvaluationDetailResultDto getPeerEvaluationDetail(Long resultId);

    // 조직 평가 내역 조회
    OrgEvaluationListResultDto getOrgEvaluations(OrgEvaluationListRequestDto requestDto);

    // 조직 평가 상세 조회
    OrgEvaluationDetailResultDto getOrgEvaluationDetail(Long resultId);

    // 자가 진단 내역 조회
    SelfEvaluationListResultDto getSelfEvaluations(SelfEvaluationListRequestDto requestDto);

    // 자가 진단 상세 조회
    SelfEvaluationDetailResultDto getSelfEvaluationDetail(Long resultId);
}