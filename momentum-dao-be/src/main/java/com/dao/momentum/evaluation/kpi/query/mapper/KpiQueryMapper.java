package com.dao.momentum.evaluation.kpi.query.mapper;

import com.dao.momentum.evaluation.kpi.query.dto.request.KpiEmployeeSummaryRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiListRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiEmployeeSummaryResponseDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiListResponseDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiDetailResponseDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KpiQueryMapper {

    // KPI 전체 목록 조회
    List<KpiListResponseDto> getKpiList(KpiListRequestDto requestDto);

    long getKpiListCount(KpiListRequestDto requestDto);

    // KPI 세부 조회
    KpiDetailResponseDto getKpiDetail(Long kpiId);

    // 사원별 KPI 조회
    List<KpiEmployeeSummaryResponseDto> getEmployeeKpiSummary(KpiEmployeeSummaryRequestDto requestDto);

    long countEmployeeKpiSummary(KpiEmployeeSummaryRequestDto requestDto);

    // KPI 제목/키워드 목록 (간략 목록용)
    // List<KpiSimpleResponseDto> getSimpleKpiList();

}
