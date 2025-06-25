package com.dao.momentum.retention.prospect.query.service;

import com.dao.momentum.retention.prospect.query.dto.request.RetentionRoundSearchRequestDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionRoundListResultDto;

public interface RetentionRoundQueryService {

    // 근속 분석 회차 목록 조회
    RetentionRoundListResultDto getRetentionRounds(RetentionRoundSearchRequestDto req);
}
