package com.dao.momentum.evaluation.kpi.query.mapper;

import com.dao.momentum.evaluation.kpi.query.dto.request.KpiDashboardRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiEmployeeSummaryRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiListRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiDashboardResponseDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiEmployeeSummaryResponseDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiListResponseDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiDetailResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    // 대시보드 KPI 조회 (DTO 통합)
    List<KpiDashboardResponseDto> getDashboardKpis(
            @Param("empId") Long empId,
            @Param("request") KpiDashboardRequestDto request
    );

    // 근속 전망을 위한 KPI 추가
    Map<String, Object> getKpiPenaltyStats(@Param("empId") Long empId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate,
                                           @Param("now") LocalDate now);

}
