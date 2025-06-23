package com.dao.momentum.evaluation.hr.query.service;

import com.dao.momentum.evaluation.hr.query.dto.request.MyHrEvaluationListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.HrEvaluationCriteriaDto;
import com.dao.momentum.evaluation.hr.query.dto.response.HrEvaluationDetailResultDto;
import com.dao.momentum.evaluation.hr.query.dto.response.HrEvaluationListResultDto;

public interface EvaluationHrService {

    // 사원 본인의 인사평가 내역(요인별 점수 포함)을 페이징 및 기간 필터와 함께 조회
    HrEvaluationListResultDto getHrEvaluations(long empId, MyHrEvaluationListRequestDto req);

    // 사원 본인의 인사 평가 세부 정보 조회
    HrEvaluationDetailResultDto getHrEvaluationDetail(Long empId, Long resultId);

    // 회차별 인사 등급 및 가중치 기준 조회
    HrEvaluationCriteriaDto getEvaluationCriteria(Integer roundNo);

}