package com.dao.momentum.work.command.application.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class WorkTimeService {
    public static final int DEFAULT_WORK_HOURS = 8;
    private static final int BREAK_30M_THRESHOLD = 4 * 60;
    private static final int BREAK_60M_THRESHOLD = 8 * 60 + 30;

    public LocalTime getStartTime() {
        return LocalTime.of(9, 0); // 회사 정보로 수정 필요
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
        LocalDate startPushedDate = startPushedAt.toLocalDate();
        LocalTime startTime = getStartTime();
        LocalDateTime startAtByPolicy = startPushedDate.atTime(startTime);
        LocalTime midTime = getMidTime();
        LocalDateTime startAtByPolicyForAMDayoff = startPushedDate.atTime(midTime);

        if (hasAMHalfDayOff) {
            return getLaterOne(startPushedAt, startAtByPolicyForAMDayoff);
        }
        return getLaterOne(startPushedAt, startAtByPolicy);
    }
}
