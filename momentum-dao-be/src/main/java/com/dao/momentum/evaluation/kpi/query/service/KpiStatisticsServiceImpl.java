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

    // KPI 전체 조회
    @Override
    @Transactional(readOnly = true)
    public KpiStatisticsResponseDto getStatistics(KpiStatisticsRequestDto requestDto) {
        log.info("[KpiStatisticsServiceImpl] getStatistics() 호출 시작 - requestDto={}", requestDto);

        KpiStatisticsResponseDto result = kpiStatisticsMapper.getMonthlyStatistics(requestDto);

        if (result == null) {
            log.error("KPI 통계 조회 실패 - requestDto={}", requestDto);
            throw new KpiException(ErrorCode.UNKNOWN_ERROR);
        }

        log.info("KPI 통계 조회 완료 - result={}", result);
        return result;
    }

    // KPI 시계열 통계 조회
    @Override
    @Transactional(readOnly = true)
    public KpiTimeseriesResponseDto getTimeseriesStatistics(KpiTimeseriesRequestDto requestDto) {
        int year = (requestDto.year() != null) ? requestDto.year() : LocalDate.now().getYear();

        // 요청 DTO 업데이트
        KpiTimeseriesRequestDto updatedDto = KpiTimeseriesRequestDto.builder()
                .year(year)
                .empNo(requestDto.empNo())
                .deptId(requestDto.deptId())
                .positionId(requestDto.positionId())
                .build();

        log.info("KPI 시계열 통계 요청 DTO - year={}, empNo={}, deptId={}, positionId={}",
                requestDto.year(), requestDto.empNo(), requestDto.deptId(), requestDto.positionId());

        List<KpiTimeseriesMonthlyDto> stats = kpiStatisticsMapper.getTimeseriesStatistics(updatedDto);

        if (stats == null) {
            log.error("KPI 시계열 통계 조회 실패 - requestDto={}", requestDto);
            throw new KpiException(ErrorCode.STATISTICS_NOT_FOUND);
        }

        log.info("KPI 시계열 통계 조회 완료 - year={}, stats.size={}", year, stats.size());
        return new KpiTimeseriesResponseDto(year, stats);
    }

    // 권한 반영된 단일 통계 조회
    @Override
    public KpiStatisticsResponseDto getStatisticsWithAccessControl(KpiStatisticsRequestDto dto, Long empId) {
        log.info("[KpiStatisticsServiceImpl] getStatisticsWithAccessControl() 호출 시작 - empId={}, dto={}", empId, dto);

        boolean isPrivileged = hasPrivilegedRole();
        log.info("사용자의 권한 여부 - isPrivileged={}", isPrivileged);

        String empNo = isPrivileged ? dto.empNo() : employeeRepository.findEmpNoByEmpId(empId);

        KpiStatisticsRequestDto resolvedDto = isPrivileged
                ? dto
                : KpiStatisticsRequestDto.builder()
                .year(dto.year())
                .month(dto.month())
                .deptId(dto.deptId())
                .positionId(dto.positionId())
                .empNo(empNo)
                .build();

        log.info("최종적으로 결정된 KPI 통계 요청 필터 - resolvedDto={}", resolvedDto);
        return getStatistics(resolvedDto);
    }

    // 권한 반영된 시계열 통계 조회
    @Override
    public KpiTimeseriesResponseDto getTimeseriesWithAccessControl(KpiTimeseriesRequestDto dto, Long empId) {
        log.info("[KpiStatisticsServiceImpl] getTimeseriesWithAccessControl() 호출 시작 - empId={}, dto={}", empId, dto);

        boolean isPrivileged = hasPrivilegedRole();
        int year = (dto.year() != null) ? dto.year() : LocalDate.now().getYear();
        String empNo = isPrivileged ? dto.empNo() : employeeRepository.findEmpNoByEmpId(empId);

        KpiTimeseriesRequestDto resolvedDto = isPrivileged
                ? KpiTimeseriesRequestDto.builder()
                .year(year)
                .empNo(dto.empNo())
                .deptId(dto.deptId())
                .positionId(dto.positionId())
                .build()
                : KpiTimeseriesRequestDto.builder()
                .year(year)
                .empNo(empNo)
                .deptId(dto.deptId())
                .positionId(dto.positionId())
                .build();

        log.info("최종적으로 결정된 KPI 시계열 통계 요청 필터 - resolvedDto={}", resolvedDto);
        return getTimeseriesStatistics(resolvedDto);
    }

    // 자신의 단일 통계 조회
    @Override
    public KpiStatisticsResponseDto getStatisticsWithControl(KpiStatisticsRequestDto dto, Long empId) {
        log.info("[KpiStatisticsServiceImpl] getStatisticsWithControl() 호출 시작 - empId={}, dto={}", empId, dto);

        String empNo = employeeRepository.findEmpNoByEmpId(empId);

        KpiStatisticsRequestDto resolvedDto = KpiStatisticsRequestDto.builder()
                .year(dto.year())
                .month(dto.month())
                .deptId(dto.deptId())
                .positionId(dto.positionId())
                .empNo(empNo)
                .build();

        log.info("최종적으로 결정된 KPI 통계 요청 필터 - resolvedDto={}", resolvedDto);
        return getStatistics(resolvedDto);
    }

    // 자신의 시계열 통계 조회
    @Override
    public KpiTimeseriesResponseDto getTimeseriesWithControl(KpiTimeseriesRequestDto dto, Long empId) {
        log.info("[KpiStatisticsServiceImpl] getTimeseriesWithControl() 호출 시작 - empId={}, dto={}", empId, dto);

        int year = (dto.year() != null) ? dto.year() : LocalDate.now().getYear();
        String empNo = employeeRepository.findEmpNoByEmpId(empId);

        KpiTimeseriesRequestDto resolvedDto = KpiTimeseriesRequestDto.builder()
                .year(year)
                .empNo(empNo)
                .deptId(dto.deptId())
                .positionId(dto.positionId())
                .build();

        log.info("최종적으로 결정된 KPI 시계열 통계 요청 필터 - resolvedDto={}", resolvedDto);
        return getTimeseriesStatistics(resolvedDto);
    }

    // 역할 확인 (권한별 공개 여부 분기용)
    private boolean hasPrivilegedRole() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> List.of("MASTER", "HR_MANAGER", "BOOKKEEPING", "MANAGER").contains(role));
    }
}
