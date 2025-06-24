package com.dao.momentum.evaluation.kpi.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.kpi.exception.KpiException;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiStatisticsRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiTimeseriesRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiStatisticsResponseDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiTimeseriesMonthlyDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiTimeseriesResponseDto;
import com.dao.momentum.evaluation.kpi.query.mapper.KpiStatisticsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KpiStatisticsServiceImpl implements KpiStatisticsService {

    private final KpiStatisticsMapper kpiStatisticsMapper;

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
                .empId(requestDto.getEmpId())
                .build();

        List<KpiTimeseriesMonthlyDto> stats = kpiStatisticsMapper.getTimeseriesStatistics(updatedDto);

        if (stats == null) {
            throw new KpiException(ErrorCode.STATISTICS_NOT_FOUND);
        }

        return new KpiTimeseriesResponseDto(year, stats);
    }

    // 권한 반영된 단일 통계 조회
    @Override
    public KpiStatisticsResponseDto getStatisticsWithAccessControl(KpiStatisticsRequestDto dto, Long requesterEmpId) {
        boolean isPrivileged = hasPrivilegedRole();
        KpiStatisticsRequestDto resolvedDto = isPrivileged
                ? dto
                : KpiStatisticsRequestDto.builder()
                .year(dto.getYear())
                .month(dto.getMonth())
                .deptId(dto.getDeptId())
                .empId(requesterEmpId)
                .build();

        return getStatistics(resolvedDto);
    }

    // 권한 반영된 시계열 통계 조회
    @Override
    public KpiTimeseriesResponseDto getTimeseriesWithAccessControl(KpiTimeseriesRequestDto dto, Long requesterEmpId) {
        boolean isPrivileged = hasPrivilegedRole();
        int year = (dto.getYear() != null) ? dto.getYear() : LocalDate.now().getYear();

        KpiTimeseriesRequestDto resolvedDto = isPrivileged
                ? KpiTimeseriesRequestDto.builder()
                .year(year)
                .empId(dto.getEmpId())
                .build()
                : KpiTimeseriesRequestDto.builder()
                .year(year)
                .empId(requesterEmpId)
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
