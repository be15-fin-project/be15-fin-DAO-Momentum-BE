package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationTaskRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationTaskListResultDto;
import com.dao.momentum.evaluation.eval.query.dto.request.NoneSubmitDto;

import java.util.List;

public interface EvaluationTaskService {
    /**
     * DTO 기반으로 평가 태스크 목록 및 전체 건수 조회
     */
    EvaluationTaskListResultDto getTasks(Long empId, EvaluationTaskRequestDto req);

    // 평가 미제출자 목록 조회
    List<NoneSubmitDto> getNoneSubmitters(Integer roundId);
}