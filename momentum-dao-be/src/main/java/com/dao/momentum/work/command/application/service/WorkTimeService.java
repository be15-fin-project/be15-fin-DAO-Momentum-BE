package com.dao.momentum.work.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.company.command.domain.aggregate.Company;
import com.dao.momentum.organization.company.command.domain.repository.CompanyRepository;
import com.dao.momentum.organization.company.exception.CompanyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkTimeService {
    public static final int DEFAULT_WORK_HOURS = 8;
    private static final int BREAK_30M_THRESHOLD = 4 * 60;
    private static final int BREAK_60M_THRESHOLD = 8 * 60 + 30;
    private final CompanyRepository companyRepository;

    public LocalTime getStartTime() {
        Company company = companyRepository.findById(1)
                .orElseThrow(() -> {
                    log.error("회사 정보 조회 실패 - 요청 일시: {}", LocalDateTime.now());
                    return new CompanyException(ErrorCode.COMPANY_INFO_NOT_FOUND);
                });
        return company.getWorkStartTime();
    }

    public LocalTime getMidTime() {
        return getStartTime().plusHours(4).plusMinutes(30);
    }

    public LocalTime getEndTime() {
        return getStartTime().plusHours(DEFAULT_WORK_HOURS).plusHours(1);
        // 휴게시간 1시간 추가
    }

    public int getBreakTime(LocalDateTime startAt, LocalDateTime endAt) {
        long diff = Duration.between(startAt, endAt).toMinutes();
        if (diff >= BREAK_60M_THRESHOLD) { // 8시간 30분부터 30분 추가 부여 필요
            return 60;
        }
        if (diff >= BREAK_30M_THRESHOLD) { // 4시간 이상 체류 시 30분의 휴게 부여
            return 30;
        }
        return 0;
    }

    public LocalDateTime getEarlierOne(LocalDateTime first, LocalDateTime second) {
        return first.isBefore(second) ? first : second;
    }

    public LocalDateTime getLaterOne(LocalDateTime first, LocalDateTime second) {
        return first.isAfter(second) ? first : second;
    }

    LocalDateTime computeStartAt(LocalDateTime startPushedAt, boolean hasAMHalfDayOff) {
        LocalDateTime truncatedStartPushedAt = startPushedAt.withSecond(0).withNano(0);
        LocalDate startPushedDate = startPushedAt.toLocalDate();
        LocalTime startTime = getStartTime();
        LocalDateTime startAtByPolicy = startPushedDate.atTime(startTime);
        LocalTime midTime = getMidTime();
        LocalDateTime startAtByPolicyForAMDayoff = startPushedDate.atTime(midTime);

        if (hasAMHalfDayOff) {
            return getLaterOne(truncatedStartPushedAt, startAtByPolicyForAMDayoff);
        }
        return getLaterOne(truncatedStartPushedAt, startAtByPolicy);
    }

    LocalDateTime computeEndAt(LocalDateTime endPushedAt, boolean hasPMHalfDayOff) {
        LocalDateTime truncatedEndPushedAt = endPushedAt.withSecond(0).withNano(0);
        LocalDate endPushedDate = endPushedAt.toLocalDate();
        LocalTime endTime = getEndTime();
        LocalDateTime endAtByPolicy = endPushedDate.atTime(endTime);
        LocalTime midTime = getMidTime();
        LocalDateTime endAtByPolicyForPMDayoff = endPushedDate.atTime(midTime);

        if (hasPMHalfDayOff) {
            return getEarlierOne(truncatedEndPushedAt, endAtByPolicyForPMDayoff);
        }
        return getEarlierOne(truncatedEndPushedAt, endAtByPolicy);
    }
}
