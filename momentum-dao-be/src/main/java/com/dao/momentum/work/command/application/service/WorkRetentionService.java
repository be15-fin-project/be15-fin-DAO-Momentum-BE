package com.dao.momentum.work.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import com.dao.momentum.work.command.application.validator.WorkCreateValidator;
import com.dao.momentum.work.command.domain.aggregate.IsNormalWork;
import com.dao.momentum.work.command.domain.aggregate.Work;
import com.dao.momentum.work.command.domain.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkRetentionService {
    private static final int MIN_SCORE_BY_ATTENDANCE = -6;

    private final EmployeeRepository employeeRepository;
    private final WorkRepository workRepository;
    private final WorkCreateValidator workCreateValidator;

    // 최대 9점 감점
    public double calculateScoreByWorkedMonths(long empId, LocalDate targetDate) {
        Employee emp = employeeRepository.findByEmpId(empId)
                .orElseThrow(() -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND));

        LocalDate joinDate = emp.getJoinDate();

        int workedMonths = calculateWorkedMonths(joinDate, targetDate);

        return minusByWorkedMonths(workedMonths);
    }

    public int calculateWorkedMonths(LocalDate joinDate, LocalDate targetDate) {
        // YearMonth 객체로 변환
        YearMonth joinYM   = YearMonth.from(joinDate);
        YearMonth targetYM = YearMonth.from(targetDate);

        // joinYM (포함) 부터 targetYM (미포함) 까지 개월 수 계산
        long monthsBetween = ChronoUnit.MONTHS.between(joinYM, targetYM);

        // 예: 입사월도 1개월로 치고 싶으면 +1 (여기선 안 침)
        return (int) monthsBetween;
    }

    private double minusByWorkedMonths(int workedMonths) {
        if (workedMonths <= 11) {
            return -7.5;
        }
        if (workedMonths <= 35) {
            return -6;
        }
        if (workedMonths <= 59) {
            return -9;
        }
        if (workedMonths <= 95) {
            return -4.5;
        }
        if (workedMonths <= 143) {
            return -1.5;
        }
        return -3;
    }

    // === (2) 무단결근·지각 기반 스코어 계산 ===
    /**
     * empId, targetDate 를 기준으로,
     *  - 기준일 포함 주의 “직전” 일요일을 periodEnd 로 잡고
     *  - 거기서 4주 전 월요일을 periodStart 로 잡아
     * 해당 기간의 무단결근 및 지각 스코어를 계산
     */
    public int calculateScoreByAbsenceAndLate(long empId, LocalDate targetDate) {
        // 기간 계산
        LocalDate periodEnd = targetDate.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
        LocalDate periodStart = periodEnd.minusWeeks(4).plusDays(1);

        // 출퇴근 데이터 조회
        List<Work> works = workRepository.findAllByEmpIdAndDateAndTypeNames(empId, periodStart, periodEnd);

        // 지각·결근 카운트
        long lateCount    = works.stream()
                .filter(w -> IsNormalWork.N == w.getIsNormalWork())
                .count();

        long absenceCount = countAbsence(works, periodStart, periodEnd);

        // 최종 스코어
        return Math.max(MIN_SCORE_BY_ATTENDANCE, getPenaltyByAbsenceAndLate(absenceCount, lateCount));
    }

    /**
     * 평일 월~금 중에
     * - 주말·휴일은 스킵
     * - workedDates 에 없으면 absence++
     */
    private long countAbsence(List<Work> works, LocalDate start, LocalDate end) {
        Set<LocalDate> workedDates = works.stream()
                .map(w -> w.getStartAt().toLocalDate())
                .collect(Collectors.toSet());

        long absence = 0;
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            if (workCreateValidator.isHoliday(d)) continue;
            if (!workedDates.contains(d)) absence++;
        }
        return absence;
    }

    /** 무단결근당 -2점, 지각 3회당 -1점 */
    private int getPenaltyByAbsenceAndLate(long absenceCount, long lateCount) {
        int absenceWeight = -2;
        int lateWeight    = -1;
        int lateUnits     = (int)(lateCount / 3);
        return (int)(absenceCount * absenceWeight + lateUnits * lateWeight);
    }

    /* 올바르게 작동하는 지 테스트 */
//    @PostConstruct
//    public void runSampleCheck() {
//        List<Long> empIds = List.of(1L, 2L, 3L,
//                11L, 12L, 13L,
//                21L, 22L, 23L,
//                31L, 32L, 33L, 34L, 35L, 36L, 37L, 38L,
//                51L, 52L, 53L, 54L, 55L, 56L, 57L, 58L, 59L, 60L);
//
//        List<LocalDate> targetDates = List.of(
//                LocalDate.of(2025, 1, 1),
//                LocalDate.of(2025, 2, 1),
//                LocalDate.of(2025, 3, 1),
//                LocalDate.of(2025, 4, 1),
//                LocalDate.of(2025, 5, 1),
//                LocalDate.of(2025, 6, 1),
//                LocalDate.of(2025, 7, 1)
//        );
//
//        List<AttendanceScoreLog> logs = new ArrayList<>();
//
//        for (long empId : empIds) {
//            for (LocalDate date : targetDates) {
//                try {
//                    int score = computeAbsenceAndLate(empId, date);
//                    logs.add(new AttendanceScoreLog(empId, date, score));
//                } catch (Exception e) {
//                    logs.add(new AttendanceScoreLog(empId, date, -999)); // 오류 표시
//                    log.warn("Failed to compute for empId={}, date={}: {}", empId, date, e.getMessage());
//                }
//            }
//        }
//
//        // 깔끔하게 한 번에 출력
//        log.info("\n========== Attendance Scores ==========");
//        logs.forEach(entry ->
//                log.info("empId={} date={} score={}", entry.empId(), entry.targetDate(), entry.score())
//        );
//        log.info("========================================");
//    }

}