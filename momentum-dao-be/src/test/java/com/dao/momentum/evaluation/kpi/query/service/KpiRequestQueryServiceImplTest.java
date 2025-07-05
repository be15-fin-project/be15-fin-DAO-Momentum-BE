package com.dao.momentum.evaluation.kpi.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.kpi.exception.KpiException;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiRequestListRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiRequestListResponseDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiRequestListResultDto;
import com.dao.momentum.evaluation.kpi.query.mapper.KpiRequestMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiRequestQueryServiceImplTest {

    @Mock
    private KpiRequestMapper kpiRequestMapper;

    @InjectMocks
    private KpiRequestQueryServiceImpl kpiRequestQueryService;

    @Test
    @DisplayName("KPI 요청 목록 조회 성공")
    void getKpiRequests_success() {
        // given
        KpiRequestListRequestDto requestDto = KpiRequestListRequestDto.builder()
                .page(1)
                .size(10)
                .statusId(2)
                .completed(false)
                .startDate("2025-06-01")
                .endDate("2025-06-30")
                .build();

        Long requesterEmpId = 123L;

        KpiRequestListResponseDto responseDto = KpiRequestListResponseDto.builder()
                .kpiId(101L)
                .empNo("20240001")
                .employeeName("김현우")
                .departmentName("기획팀")
                .positionName("대리")
                .goal("월간 영업 건수 10건 달성")
                .goalValue(10)
                .kpiProgress(40)
                .statusName("대기중")
                .createdAt("2025-06-01")
                .deadline("2025-06-30")
                .build();

        List<KpiRequestListResponseDto> mockList = List.of(responseDto);

        when(kpiRequestMapper.countKpiRequests(any())).thenReturn(1);
        when(kpiRequestMapper.findKpiRequests(any())).thenReturn(mockList);

        // when
        KpiRequestListResultDto result = kpiRequestQueryService.getKpiRequests(requesterEmpId, requestDto);

        // then
        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(1, result.pagination().getTotalItems());

        verify(kpiRequestMapper, times(1)).countKpiRequests(any());
        verify(kpiRequestMapper, times(1)).findKpiRequests(any());
    }

    @Test
    @DisplayName("KPI 요청 목록 조회 실패 - null 결과")
    void getKpiRequests_null_throwsException() {
        // given
        KpiRequestListRequestDto requestDto = KpiRequestListRequestDto.builder()
                .page(1)
                .size(10)
                .build();

        Long requesterEmpId = 123L;

        when(kpiRequestMapper.countKpiRequests(any())).thenReturn(0);
        when(kpiRequestMapper.findKpiRequests(any())).thenReturn(null);

        // when & then
        KpiException exception = assertThrows(KpiException.class, () ->
                kpiRequestQueryService.getKpiRequests(requesterEmpId, requestDto)
        );

        assertEquals(ErrorCode.KPI_REQUEST_NOT_FOUND, exception.getErrorCode());
        verify(kpiRequestMapper).countKpiRequests(any());
        verify(kpiRequestMapper).findKpiRequests(any());
    }

}
