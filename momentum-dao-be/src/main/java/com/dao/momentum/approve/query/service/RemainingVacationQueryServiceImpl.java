package com.dao.momentum.approve.query.service;

import com.dao.momentum.approve.query.dto.response.DayoffResponse;
import com.dao.momentum.approve.query.dto.response.RefreshResponse;
import com.dao.momentum.approve.query.mapper.VacationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RemainingVacationQueryServiceImpl implements RemainingVacationQueryService {
    private final VacationMapper vacationMapper;

    @Override
    public DayoffResponse getRemainingDayoffs(long empId) {
        int remainingDayoffHours = vacationMapper.getRemainingDayoffs(empId);

        return DayoffResponse.builder()
                .empId(empId)
                .remainingDayoffHours(remainingDayoffHours)
                .build();
    }

    @Override
    public RefreshResponse getRemainingRefreshs(long empId) {
        int remainingRefreshDays = vacationMapper.getRemainingRefreshs(empId);

        return RefreshResponse.builder()
                .empId(empId)
                .remainingRefreshDays(remainingRefreshDays)
                .build();
    }
}
