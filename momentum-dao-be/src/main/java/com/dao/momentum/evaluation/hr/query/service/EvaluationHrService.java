package com.dao.momentum.evaluation.hr.query.service;

import com.dao.momentum.evaluation.hr.query.dto.request.MyHrEvaluationListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.HrEvaluationListResultDto;

public interface EvaluationHrService {

    // 사원 본인의 인사평가 내역(요인별 점수 포함)을 페이징 및 기간 필터와 함께 조회
    HrEvaluationListResultDto getHrEvaluations(long empId, MyHrEvaluationListRequestDto req);
}