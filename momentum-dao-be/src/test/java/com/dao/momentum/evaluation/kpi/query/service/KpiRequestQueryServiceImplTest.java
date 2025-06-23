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
        KpiRequestListRequestDto requestDto = new KpiRequestListRequestDto();
        requestDto.setPage(1);
        requestDto.setSize(10);
        requestDto.setStatusId(2);
        requestDto.setCompleted(false);
        requestDto.setStartDate("2025-06-01");
        requestDto.setEndDate("2025-06-30");

        Long requesterEmpId = 123L;

        List<KpiRequestListResponseDto> mockList = List.of(new KpiRequestListResponseDto());
        when(kpiRequestMapper.countKpiRequests(any())).thenReturn(1);
        when(kpiRequestMapper.findKpiRequests(any())).thenReturn(mockList);

        // when
        KpiRequestListResultDto result = kpiRequestQueryService.getKpiRequests(requesterEmpId, requestDto);

        // then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getPagination().getTotalItems());

        verify(kpiRequestMapper, times(1)).countKpiRequests(any());
        verify(kpiRequestMapper, times(1)).findKpiRequests(any());
    }

    @Test
    @DisplayName("KPI 요청 목록 조회 실패 - 결과 없음")
    void getKpiRequests_empty_throwsException() {
        // given
        KpiRequestListRequestDto requestDto = new KpiRequestListRequestDto();
        requestDto.setPage(1);
        requestDto.setSize(10);
        Long requesterEmpId = 123L;

        when(kpiRequestMapper.countKpiRequests(any())).thenReturn(0);
        when(kpiRequestMapper.findKpiRequests(any())).thenReturn(Collections.emptyList());

        // when & then
        KpiException exception = assertThrows(KpiException.class, () -> {
            kpiRequestQueryService.getKpiRequests(requesterEmpId, requestDto);
        });

        assertEquals(ErrorCode.KPI_REQUEST_NOT_FOUND, exception.getErrorCode());
        verify(kpiRequestMapper).countKpiRequests(any());
        verify(kpiRequestMapper).findKpiRequests(any());
    }
}
