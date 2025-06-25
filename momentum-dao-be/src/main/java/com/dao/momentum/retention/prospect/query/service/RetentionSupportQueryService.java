package com.dao.momentum.retention.prospect.query.service;

import com.dao.momentum.retention.prospect.query.dto.request.RetentionForecastRequestDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionForecastResponseDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionSupportDetailDto;

public interface RetentionSupportQueryService {

    // 근속 전망 목록 조회
    RetentionForecastResponseDto getRetentionForecasts(RetentionForecastRequestDto req);

    // 근속 전망 상세 조회
    RetentionSupportDetailDto getSupportDetail(Long retentionId);

}
