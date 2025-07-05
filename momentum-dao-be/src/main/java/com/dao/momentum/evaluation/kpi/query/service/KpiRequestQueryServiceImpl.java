package com.dao.momentum.evaluation.kpi.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.kpi.exception.KpiException;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiRequestListRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiRequestListResponseDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiRequestListResultDto;
import com.dao.momentum.evaluation.kpi.query.mapper.KpiRequestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KpiRequestQueryServiceImpl implements KpiRequestQueryService {

    private final KpiRequestMapper kpiRequestMapper;

    // KPI 요청 목록을 조회하는 메서드
    @Override
    @Transactional(readOnly = true)
    public KpiRequestListResultDto getKpiRequests(Long requesterEmpId, KpiRequestListRequestDto requestDto) {
        log.info("[KpiRequestQueryServiceImpl] getKpiRequests() 호출 시작 - requesterEmpId={}, requestDto={}", requesterEmpId, requestDto);

        // 요청 조건 업데이트
        KpiRequestListRequestDto updatedDto = KpiRequestListRequestDto.builder()
                .requesterEmpId(requesterEmpId)
                .statusId(requestDto.statusId())
                .completed(requestDto.completed())
                .empNo(requestDto.empNo())
                .startDate(requestDto.startDate())
                .endDate(requestDto.endDate())
                .page(requestDto.page())
                .size(requestDto.size())
                .build();

        log.info("KPI 요청 목록 조회 조건 업데이트 완료 - updatedDto={}", updatedDto);

        // 1) 전체 건수 조회
        long total = kpiRequestMapper.countKpiRequests(updatedDto);
        log.info("전체 KPI 요청 건수 조회 완료 - total={}", total);

        // 2) KPI 요청 목록 조회
        List<KpiRequestListResponseDto> list = kpiRequestMapper.findKpiRequests(updatedDto);

        // 요청 목록이 없으면 예외 처리
        if (list == null || list.isEmpty()) {
            log.error("KPI 요청 목록을 찾을 수 없음 - updatedDto={}", updatedDto);
            throw new KpiException(ErrorCode.KPI_REQUEST_NOT_FOUND);
        }

        // 3) 페이지네이션 계산
        int totalPage = (int) Math.ceil((double) total / requestDto.size());
        Pagination pagination = Pagination.builder()
                .currentPage(requestDto.page())
                .totalPage(totalPage)
                .totalItems(total)
                .build();
        log.info("페이지네이션 정보 생성 완료 - currentPage={}, totalPage={}, totalItems={}",
                pagination.getCurrentPage(), pagination.getTotalPage(), pagination.getTotalItems());

        // 결과 반환
        KpiRequestListResultDto result = new KpiRequestListResultDto(list, pagination);
        log.info("KPI 요청 목록 조회 완료 - result={}", result);

        return result;
    }
}
