package com.dao.momentum.evaluation.kpi.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.kpi.exception.KpiException;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiEmployeeSummaryRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiListRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.*;
import com.dao.momentum.evaluation.kpi.query.mapper.KpiQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KpiQueryServiceImpl implements KpiQueryService {

    private final KpiQueryMapper kpiQueryMapper;

    // KPI 전체 조회
    @Override
    @Transactional(readOnly = true)
    public KpiListResultDto getKpiList(KpiListRequestDto requestDto) {
        log.info("KPI 목록 요청: {}", requestDto);
        long total = kpiQueryMapper.getKpiListCount(requestDto);
        List<KpiListResponseDto> list = kpiQueryMapper.getKpiList(requestDto);

        if (list == null) {
            throw new KpiException(ErrorCode.KPI_LIST_NOT_FOUND);
        }

        Pagination pagination = buildPagination(requestDto.getPage(), requestDto.getSize(), total);


        return new KpiListResultDto(list, pagination);
    }

    // KPI 세부 조회
    @Override
    @Transactional(readOnly = true)
    public KpiDetailResponseDto getKpiDetail(Long kpiId) {
        KpiDetailResponseDto detail = kpiQueryMapper.getKpiDetail(kpiId);
        if (detail == null) {
            throw new KpiException(ErrorCode.KPI_NOT_FOUND);
        }
        return detail;
    }

    // 사원별 KPI 조회
    @Override
    @Transactional(readOnly = true)
    public KpiEmployeeSummaryResultDto getEmployeeKpiSummaries(KpiEmployeeSummaryRequestDto requestDto) {

        log.info("사원별 KPI 조회 요청 필터값: empNo={}, deptId={}, year={}, month={}, page={}, size={}",
                requestDto.getEmpNo(), requestDto.getDeptId(), requestDto.getYear(), requestDto.getMonth(), requestDto.getPage(), requestDto.getSize());
        long total = kpiQueryMapper.countEmployeeKpiSummary(requestDto);
        List<KpiEmployeeSummaryResponseDto> list = kpiQueryMapper.getEmployeeKpiSummary(requestDto);

        if (list == null) {
            throw new KpiException(ErrorCode.KPI_EMPLOYEE_SUMMARY_NOT_FOUND);
        }

        Pagination pagination = buildPagination(requestDto.getPage(), requestDto.getSize(), total);


        return new KpiEmployeeSummaryResultDto(list, pagination);
    }

    // 페이지네이션
    private Pagination buildPagination(int page, int size, long total) {
        int totalPage = (int) Math.ceil((double) total / size);
        return Pagination.builder()
                .currentPage(page)
                .totalPage(totalPage)
                .totalItems(total)
                .build();
    }

    // 권한 반영
    @Override
    public KpiListResultDto getKpiListWithAccessControl(KpiListRequestDto requestDto, Long requesterEmpId, String empNo) {
        boolean isPrivileged = hasPrivilegedRole();

        KpiListRequestDto resolved = isPrivileged
                ? requestDto
                : KpiListRequestDto.builder()
                .empNo(empNo)
                .deptId(requestDto.getDeptId())
                .positionId(requestDto.getPositionId())
                .statusId(requestDto.getStatusId())
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .page(requestDto.getPage() != null ? requestDto.getPage() : 1)
                .size(requestDto.getSize() != null ? requestDto.getSize() : 10)
                .build();

        return getKpiList(resolved);
    }

    private boolean hasPrivilegedRole() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> List.of("MASTER", "HR_MANAGER", "BOOKKEEPING", "MANAGER").contains(role));
    }



}