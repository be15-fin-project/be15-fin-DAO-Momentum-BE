package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationTaskRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationTaskListResultDto;

public interface EvaluationTaskService {
    /**
     * DTO 기반으로 평가 태스크 목록 및 전체 건수 조회
     */
    EvaluationTaskListResultDto getTasks(Long empId, EvaluationTaskRequestDto req);
}