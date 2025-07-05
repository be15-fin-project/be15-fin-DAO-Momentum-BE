package com.dao.momentum.evaluation.kpi.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.kpi.exception.KpiException;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiEmployeeSummaryRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiListRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.*;
import com.dao.momentum.evaluation.kpi.query.mapper.KpiQueryMapper;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
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
    private final EmployeeRepository employeeRepository;

    // KPI 전체 조회
    @Override
    @Transactional(readOnly = true)
    public KpiListResultDto getKpiList(KpiListRequestDto requestDto) {
        log.info("[KpiQueryServiceImpl] getKpiList() 호출 시작 - requestDto={}", requestDto);

        long total = kpiQueryMapper.getKpiListCount(requestDto);
        log.info("전체 KPI 건수 조회 완료 - total={}", total);

        List<KpiListResponseDto> list = kpiQueryMapper.getKpiList(requestDto);
        if (list == null) {
            log.error("KPI 목록을 찾을 수 없음 - requestDto={}", requestDto);
            throw new KpiException(ErrorCode.KPI_LIST_NOT_FOUND);
        }

        Pagination pagination = buildPagination(requestDto.getPage(), requestDto.getSize(), total);
        log.info(" 페이지네이션 정보 생성 완료 - currentPage={}, totalPage={}, totalItems={}",
                pagination.getCurrentPage(), pagination.getTotalPage(), pagination.getTotalItems());

        return new KpiListResultDto(list, pagination);
    }

    // KPI 세부 조회
    @Override
    @Transactional(readOnly = true)
    public KpiDetailResponseDto getKpiDetail(Long kpiId) {
        log.info("[KpiQueryServiceImpl] getKpiDetail() 호출 시작 - kpiId={}", kpiId);

        KpiDetailResponseDto detail = kpiQueryMapper.getKpiDetail(kpiId);
        if (detail == null) {
            log.error("KPI 세부 정보를 찾을 수 없음 - kpiId={}", kpiId);
            throw new KpiException(ErrorCode.KPI_NOT_FOUND);
        }

        log.info("KPI 세부 조회 완료 - kpiId={}", kpiId);
        return detail;
    }

    // 사원별 KPI 조회
    @Override
    @Transactional(readOnly = true)
    public KpiEmployeeSummaryResultDto getEmployeeKpiSummaries(KpiEmployeeSummaryRequestDto requestDto) {
        log.info("[KpiQueryServiceImpl] getEmployeeKpiSummaries() 호출 시작 - requestDto={}", requestDto);

        long total = kpiQueryMapper.countEmployeeKpiSummary(requestDto);
        log.info("사원별 KPI 총 건수 조회 완료 - total={}", total);

        List<KpiEmployeeSummaryResponseDto> list = kpiQueryMapper.getEmployeeKpiSummary(requestDto);
        if (list == null) {
            log.error("사원별 KPI 목록을 찾을 수 없음 - requestDto={}", requestDto);
            throw new KpiException(ErrorCode.KPI_EMPLOYEE_SUMMARY_NOT_FOUND);
        }

        Pagination pagination = buildPagination(requestDto.getPage(), requestDto.getSize(), total);
        log.info("페이지네이션 정보 생성 완료 - currentPage={}, totalPage={}, totalItems={}",
                pagination.getCurrentPage(), pagination.getTotalPage(), pagination.getTotalItems());

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
    public KpiListResultDto getKpiListWithAccessControl(KpiListRequestDto requestDto, Long empId) {
        log.info("[KpiQueryServiceImpl] getKpiListWithAccessControl() 호출 시작 - empId={}, requestDto={}", empId, requestDto);

        boolean isPrivileged = hasPrivilegedRole();
        log.info("사용자의 권한 여부 - isPrivileged={}", isPrivileged);

        String empNo = employeeRepository.findEmpNoByEmpId(empId);
        log.info("사원 번호 조회 완료 - empNo={}", empNo);

        KpiListRequestDto resolved = isPrivileged
                ? requestDto
                : KpiListRequestDto.builder()
                .empNo(empNo)
                .deptId(requestDto.getDeptId())
                .positionId(requestDto.getPositionId())
                .statusId(requestDto.getStatusId())
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .isDeleted(requestDto.getIsDeleted())
                .page(requestDto.getPage() != null ? requestDto.getPage() : 1)
                .size(requestDto.getSize() != null ? requestDto.getSize() : 10)
                .build();

        log.info("최종적으로 결정된 KPI 요청  필터 - resolved={}", resolved);
        return getKpiList(resolved);
    }

    // 권한 반영
    @Override
    public KpiListResultDto getKpiListWithControl(KpiListRequestDto requestDto, Long empId) {
        log.info("[KpiQueryServiceImpl] getKpiListWithControl() 호출 시작 - empId={}, requestDto={}", empId, requestDto);

        String empNo = employeeRepository.findEmpNoByEmpId(empId);
        log.info("사원번호 조회 완료 - empNo={}", empNo);

        KpiListRequestDto resolved = KpiListRequestDto.builder()
                .empNo(empNo)
                .deptId(requestDto.getDeptId())
                .positionId(requestDto.getPositionId())
                .statusId(requestDto.getStatusId())
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .isDeleted(requestDto.getIsDeleted())
                .page(requestDto.getPage() != null ? requestDto.getPage() : 1)
                .size(requestDto.getSize() != null ? requestDto.getSize() : 10)
                .build();

        log.info("최종적으로 결정된 KPI 요청 필터 - resolved={}", resolved);
        return getKpiList(resolved);
    }

    private boolean hasPrivilegedRole() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> List.of("MASTER", "HR_MANAGER", "BOOKKEEPING", "MANAGER").contains(role));
    }
}
