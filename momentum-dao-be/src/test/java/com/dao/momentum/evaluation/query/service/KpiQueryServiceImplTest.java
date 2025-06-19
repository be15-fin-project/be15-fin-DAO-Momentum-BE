package com.dao.momentum.evaluation.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.exception.KpiException;
import com.dao.momentum.evaluation.query.dto.request.KpiListRequestDto;
import com.dao.momentum.evaluation.query.dto.response.KpiDetailResponseDto;
import com.dao.momentum.evaluation.query.dto.response.KpiListResponseDto;
import com.dao.momentum.evaluation.query.dto.response.KpiListResultDto;
import com.dao.momentum.evaluation.query.mapper.KpiQueryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KpiQueryServiceImplTest {

    private KpiQueryMapper kpiQueryMapper;
    private KpiQueryServiceImpl kpiQueryService;

    private Long kpiId;

    @BeforeEach
    void setUp() {
        kpiQueryMapper = mock(KpiQueryMapper.class);
        kpiQueryService = new KpiQueryServiceImpl(kpiQueryMapper);
        kpiId = 101L;
    }

    @Test
    @DisplayName("KPI 전체 내역 조회 - 정상 케이스")
    void getKpiList_success() {
        // given
        KpiListRequestDto requestDto = new KpiListRequestDto();
        requestDto.setPage(1);
        requestDto.setSize(10);

        KpiListResponseDto mockItem = new KpiListResponseDto();
        mockItem.setKpiId(1L);
        mockItem.setEmpNo("HR001");
        mockItem.setEmployeeName("정예준");

        when(kpiQueryMapper.getKpiListCount(requestDto)).thenReturn(1L);
        when(kpiQueryMapper.getKpiList(requestDto)).thenReturn(List.of(mockItem));

        // when
        var result = kpiQueryService.getKpiList(requestDto);

        // then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("HR001", result.getContent().get(0).getEmpNo());

        Pagination pagination = result.getPagination();
        assertEquals(1, pagination.getTotalItems());
        assertEquals(1, pagination.getTotalPage());
        assertEquals(1, pagination.getCurrentPage());
    }

    @Test
    @DisplayName("KPI 전체 내역 조회 - 결과 없음 (빈 리스트)")
    void getKpiList_emptyResult_shouldThrowException() {
        // given
        KpiListRequestDto requestDto = new KpiListRequestDto();
        requestDto.setPage(1);
        requestDto.setSize(10);

        when(kpiQueryMapper.getKpiListCount(requestDto)).thenReturn(0L);
        when(kpiQueryMapper.getKpiList(requestDto)).thenReturn(Collections.emptyList());

        // when & then
        KpiException ex = assertThrows(KpiException.class, () -> {
            kpiQueryService.getKpiList(requestDto);
        });

        assertEquals(ErrorCode.KPI_LIST_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("KPI 전체 내역 조회 - null 반환 시 예외")
    void getKpiList_nullResult_shouldThrowException() {
        // given
        KpiListRequestDto requestDto = new KpiListRequestDto();
        requestDto.setPage(1);
        requestDto.setSize(10);

        when(kpiQueryMapper.getKpiListCount(requestDto)).thenReturn(5L);
        when(kpiQueryMapper.getKpiList(requestDto)).thenReturn(null);

        // when & then
        KpiException ex = assertThrows(KpiException.class, () -> {
            kpiQueryService.getKpiList(requestDto);
        });

        assertEquals(ErrorCode.KPI_LIST_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("KPI 세부 조회 성공")
    void getKpiDetail_success() {
        // given
        KpiDetailResponseDto mockDto = new KpiDetailResponseDto();
        mockDto.setKpiId(kpiId);
        mockDto.setEmpNo("HR001");
        mockDto.setEmployeeName("정예준");
        mockDto.setDepartmentName("기획팀");
        mockDto.setPositionName("대리");
        mockDto.setGoal("리포트 자동화");
        mockDto.setGoalValue(5);
        mockDto.setKpiProgress(75);
        mockDto.setProgress25("템플릿 작성");
        mockDto.setProgress50("API 연동");
        mockDto.setProgress75("리포트 생성");
        mockDto.setProgress100("운영 반영");
        mockDto.setStatusType("ACCEPTED");
        mockDto.setCreatedAt("2025-05-01");
        mockDto.setDeadline("2025-06-30");

        when(kpiQueryMapper.getKpiDetail(kpiId)).thenReturn(mockDto);

        // when
        KpiDetailResponseDto result = kpiQueryService.getKpiDetail(kpiId);

        // then
        assertNotNull(result);
        assertEquals("정예준", result.getEmployeeName());
        assertEquals(75, result.getKpiProgress());
    }

    @Test
    @DisplayName("KPI 세부 조회 실패 - 존재하지 않음")
    void getKpiDetail_notFound_throwsException() {
        when(kpiQueryMapper.getKpiDetail(kpiId)).thenReturn(null);

        KpiException exception = assertThrows(KpiException.class, () -> {
            kpiQueryService.getKpiDetail(kpiId);
        });

        assertEquals(ErrorCode.KPI_NOT_FOUND, exception.getErrorCode());
    }
}
