package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.evaluation.eval.query.dto.response.EvalFormDetailResultDto;

public interface EvalFormQueryService {
    EvalFormDetailResultDto getFormDetail(Integer formId, Integer roundId);
}
