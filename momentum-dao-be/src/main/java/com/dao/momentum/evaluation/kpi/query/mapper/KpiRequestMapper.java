package com.dao.momentum.evaluation.kpi.query.mapper;

import com.dao.momentum.evaluation.kpi.query.dto.request.KpiRequestListRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiRequestListResponseDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KpiRequestMapper {

    // KPI 요청 목록 조회
    List<KpiRequestListResponseDto> findKpiRequests(KpiRequestListRequestDto requestDto);

    int countKpiRequests(KpiRequestListRequestDto requestDto);
}
