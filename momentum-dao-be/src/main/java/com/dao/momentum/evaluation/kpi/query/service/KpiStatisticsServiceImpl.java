package com.dao.momentum.evaluation.kpi.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.kpi.exception.KpiException;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiStatisticsRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiTimeseriesRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiStatisticsResponseDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiTimeseriesMonthlyDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiTimeseriesResponseDto;
import com.dao.momentum.evaluation.kpi.query.mapper.KpiStatisticsMapper;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j

@Service
@RequiredArgsConstructor
public class KpiStatisticsServiceImpl implements KpiStatisticsService {

    private final KpiStatisticsMapper kpiStatisticsMapper;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public KpiStatisticsResponseDto getStatistics(KpiStatisticsRequestDto requestDto) {
        KpiStatisticsResponseDto result = kpiStatisticsMapper.getMonthlyStatistics(requestDto);

        if (result == null) {
            throw new KpiException(ErrorCode.UNKNOWN_ERROR);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public KpiTimeseriesResponseDto getTimeseriesStatistics(KpiTimeseriesRequestDto requestDto) {
        int year = (requestDto.getYear() != null) ? requestDto.getYear() : LocalDate.now().getYear();

        KpiTimeseriesRequestDto updatedDto = KpiTimeseriesRequestDto.builder()
                .year(year)
                .empNo(requestDto.getEmpNo())
                .build();

        List<KpiTimeseriesMonthlyDto> stats = kpiStatisticsMapper.getTimeseriesStatistics(updatedDto);
        log.info("KPI 통계 요청 DTO: year={}, empNo={}, deptId={}, positionId={}",
                requestDto.getYear(), requestDto.getEmpNo(), requestDto.getDeptId(), requestDto.getPositionId());

        if (stats == null) {
            throw new KpiException(ErrorCode.STATISTICS_NOT_FOUND);
        }

        return new KpiTimeseriesResponseDto(year, stats);
    }

    // 권한 반영된 단일 통계 조회
    @Override
    public KpiStatisticsResponseDto getStatisticsWithAccessControl(KpiStatisticsRequestDto dto, Long empId) {
        boolean isPrivileged = hasPrivilegedRole();
        String empNo = isPrivileged ? dto.getEmpNo() : employeeRepository.findEmpNoByEmpId(empId);

        KpiStatisticsRequestDto resolvedDto = isPrivileged
                ? dto
                : KpiStatisticsRequestDto.builder()
                .year(dto.getYear())
                .month(dto.getMonth())
                .deptId(dto.getDeptId())
                .empNo(empNo)
                .build();

        return getStatistics(resolvedDto);
    }

    // 권한 반영된 시계열 통계 조회
    @Override
    public KpiTimeseriesResponseDto getTimeseriesWithAccessControl(KpiTimeseriesRequestDto dto, Long empId) {
        boolean isPrivileged = hasPrivilegedRole();
        int year = (dto.getYear() != null) ? dto.getYear() : LocalDate.now().getYear();
        String empNo = isPrivileged ? dto.getEmpNo() : employeeRepository.findEmpNoByEmpId(empId);

        KpiTimeseriesRequestDto resolvedDto = isPrivileged
                ? KpiTimeseriesRequestDto.builder()
                .year(year)
                .empNo(dto.getEmpNo())
                .build()
                : KpiTimeseriesRequestDto.builder()
                .year(year)
                .empNo(empNo)
                .build();

        return getTimeseriesStatistics(resolvedDto);
    }

    // 역할 확인 (권한별 공개 여부 분기용)
    private boolean hasPrivilegedRole() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> List.of("MASTER", "HR_MANAGER", "BOOKKEEPING", "MANAGER").contains(role));
    }
}
