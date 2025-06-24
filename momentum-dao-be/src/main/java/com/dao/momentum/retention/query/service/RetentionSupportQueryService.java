package com.dao.momentum.retention.query.service;

import com.dao.momentum.retention.query.dto.request.RetentionForecastRequestDto;
import com.dao.momentum.retention.query.dto.response.RetentionForecastResponseDto;

public interface RetentionSupportQueryService {

    // 근속 전망 목록 조회
    RetentionForecastResponseDto getRetentionForecasts(RetentionForecastRequestDto req);
}
