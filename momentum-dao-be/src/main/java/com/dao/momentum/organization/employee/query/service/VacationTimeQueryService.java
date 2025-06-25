package com.dao.momentum.organization.employee.query.service;

import com.dao.momentum.organization.employee.query.dto.request.VacationComputeDTO;
import com.dao.momentum.organization.employee.query.dto.request.VacationComputeRequest;
import com.dao.momentum.organization.employee.query.dto.response.VacationTimesDTO;
import com.dao.momentum.organization.employee.query.dto.response.VacationTimesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class VacationTimeQueryService {
    private static final int WORKING_HOURS_IN_DAY = 8;
    private static final int MONTHS_IN_YEAR = 12;
    private static final int STANDARD_DAYOFF = 15;
    private static final int MAXIMUM_DAYOFF = 25;

    public VacationTimesResponse getVacationTimes(VacationComputeRequest vacationComputeRequest) {
        VacationComputeDTO vacationComputeDTO = VacationComputeDTO.fromRequest(vacationComputeRequest);

        int joinYear = vacationComputeDTO.getJoinYear();
        int joinMonth = vacationComputeDTO.getJoinMonth();
        int targetYear = vacationComputeDTO.getTargetYear();

        int dayoffHours = computeDayoffHours(joinYear, joinMonth, targetYear);
        int refreshDays = computeRefreshDays(joinYear, joinMonth, targetYear);


        VacationTimesDTO vacationTimes = VacationTimesDTO.builder()
                .dayoffHours(dayoffHours)
                .refreshDays(refreshDays)
                .build();

        return VacationTimesResponse.builder()
                .vacationTimes(vacationTimes)
                .build();
    }

    private int computeDayoffDays(int joinYear, int joinMonth, int targetYear) {
        int yearsWorked = targetYear - joinYear;

        if (yearsWorked < 0) {
            return 0;
        }

        if (yearsWorked == 0) { // 입사한 해
            return Math.max(0, MONTHS_IN_YEAR - joinMonth - 1);
        }

        if (yearsWorked == 1) {
            double weight = (double) joinMonth / MONTHS_IN_YEAR;
            int workedMonths = MONTHS_IN_YEAR - joinMonth + 1;

            return (int) Math.ceil(
                    STANDARD_DAYOFF * (1 - weight) + workedMonths * weight
            );
        }

        // 2년차 이상 -> 3년차부터, 2년마다 1일 추가 지급
        int dayoffDays = STANDARD_DAYOFF + (yearsWorked - 2) / 2;
        return Math.min(MAXIMUM_DAYOFF, dayoffDays);
    }

    private int computeDayoffHours(int joinYear, int joinMonth, int targetYear) {
        return computeDayoffDays(joinYear, joinMonth, targetYear) * WORKING_HOURS_IN_DAY;
    }

    private int computeRefreshDays(int joinYear, int joinMonth, int targetYear) {
        int yearsWorked = targetYear - joinYear;

        return switch (yearsWorked) {
            case 3 -> 3;
            case 5 -> 5;
            case 10 -> 10;
            default -> 0;
        };
    }


}
