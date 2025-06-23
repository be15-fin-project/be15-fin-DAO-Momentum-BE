package com.dao.momentum.evaluation.hr.query.service;

import com.dao.momentum.evaluation.hr.query.dto.request.HrObjectionListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.HrObjectionListResultDto;

public interface HrObjectionQueryService {
    /**
     * 팀장이 작성한 사원의 인사 평가 이의제기 목록 조회
     */
    HrObjectionListResultDto getObjections(HrObjectionListRequestDto req);
}
