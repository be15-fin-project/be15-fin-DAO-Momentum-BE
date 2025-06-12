package com.dao.momentum.work.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.work.command.application.dto.request.WorkStartRequest;
import com.dao.momentum.work.command.application.dto.response.WorkCreateResponse;
import com.dao.momentum.work.command.application.dto.response.WorkSummaryDTO;
import com.dao.momentum.work.command.domain.aggregate.IsNormalWork;
import com.dao.momentum.work.command.domain.aggregate.Work;
import com.dao.momentum.work.command.domain.aggregate.WorkType;
import com.dao.momentum.work.command.domain.aggregate.WorkTypeName;
import com.dao.momentum.work.command.domain.repository.WorkRepository;
import com.dao.momentum.work.command.domain.repository.WorkTypeRepository;
import com.dao.momentum.work.exception.WorkException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.util.SubnetUtils;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkCommandService {
    private static final int DEFAULT_WORK_HOURS = 8;
    private static final int BREAK_30M_THRESHOLD = 4 * 60;
    private static final int BREAK_60M_THRESHOLD = 8 * 60 + 30;

    private final WorkTypeRepository workTypeRepository;
    private final WorkRepository workRepository;

    public WorkCreateResponse createWork(WorkStartRequest workStartRequest, HttpServletRequest httpServletRequest) {
        String ip = extractClientIp(httpServletRequest);
        log.info("출근 등록 요청 - IP: {}, 요청 시각: {}", ip, workStartRequest.getStartPushedAt());

        validateIp(ip);
        long empId = 1; // 로그인 구현 후 수정
        LocalDate today = LocalDate.now();

        boolean hasAMHalfDayoff = hasAMHalfDayOff(empId, today);
        boolean hasPMHalfDayoff = hasPMHalfDayOff(empId, today);


        if (workAlreadyRecorded(empId, today, WorkTypeName.WORK.name())) {
            throw new WorkException(ErrorCode.WORK_ALREADY_RECORDED);
        }

        if (hasApprovedWork(empId, today)) {
            throw new WorkException(ErrorCode.ACCEPTED_WORK_ALREADY_RECORDED);
        }

        if (isHoliday(today)) {
            throw new WorkException(ErrorCode.WORK_REQUESTED_ON_HOLIDAY);
        }

        WorkType workType = workTypeRepository.findByTypeName(WorkTypeName.WORK.name())
                .orElseThrow(() -> {
                    log.error("WorkType '{}'을(를) 찾을 수 없음", WorkTypeName.WORK.name());
                    return new WorkException(ErrorCode.WORKTYPE_NOT_FOUND);
                });

        int typeId = workType.getTypeId();

        LocalDateTime startPushedAt = workStartRequest.getStartPushedAt();
        LocalDateTime startAt = computeStartAt(startPushedAt, hasAMHalfDayoff);
        LocalDateTime endAt = hasPMHalfDayoff? LocalDateTime.of(startPushedAt.toLocalDate(), getMidTime()) : LocalDateTime.of(startPushedAt.toLocalDate(), getEndTime());
        validateStartAt(startAt, endAt);

        int breakTime = getBreakTime(startAt, endAt);

        Work work = Work.builder()
                .empId(empId)
                .typeId(typeId)
                .startAt(startAt)
                .endAt(endAt)
                .startPushedAt(startPushedAt)
                .breakTime(breakTime)
                .build();

        IsNormalWork isNormalWork = work.getWorkTime().toHours() >= DEFAULT_WORK_HOURS ? IsNormalWork.Y : IsNormalWork.N;
        work.setIsNormalWork(isNormalWork);

        WorkSummaryDTO workSummaryDTO = WorkSummaryDTO.from(work);

        workRepository.save(work);
        log.info("출근 등록 완료 - workId: {}, empId: {}, 등록일시: {}", work.getWorkId(), empId, startPushedAt);


        return WorkCreateResponse.builder()
                .workSummaryDTO(workSummaryDTO)
                .message("출근 등록 성공")
                .build();
    }

    private List<String> getAllowedIps() {
//        return List.of();
        return List.of("0.0.0.0/0");
    }

    private void validateIp(String ip) {
        List<String> allowedIps = getAllowedIps();
        boolean allowed = allowedIps.stream().anyMatch(allowedIp -> {
            try {
                if (allowedIp.contains("/")) {
                    SubnetUtils subnet = new SubnetUtils(allowedIp);
                    subnet.setInclusiveHostCount(true);
                    return subnet.getInfo().isInRange(ip);
                }
                return allowedIp.equals(ip);
            } catch (IllegalArgumentException e) {
                log.warn("잘못된 IP 형식: {}", allowedIp);
                return false;
            }
        });

        if (!allowed) throw new WorkException(ErrorCode.IP_NOT_ALLOWED);
    }

    /* 프록시 IP에 대한 처리 필요 */
    private String extractClientIp(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "X-Real-IP"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }

    private void validateStartAt(LocalDateTime startAt, LocalDateTime endAt) {
        LocalDate today = LocalDate.now();

        if (!startAt.toLocalDate().isEqual(today)) {
            log.warn("잘못된 출근 요청 - 출근 시각이 오늘 날짜가 아님: {}", startAt.toLocalDate());
            throw new WorkException(ErrorCode.INVALID_WORK_TIME);
        }

        if (startAt.isAfter(endAt)) {
            log.warn("잘못된 출근 요청 - 출근 시각이 종료 시각보다 늦음: startAt={}, endAt={}", startAt, endAt);
            throw new WorkException(ErrorCode.INVALID_WORK_TIME);
        }

    }

    // 이미 등록된 출근 기록이 있는 지 체크
    private boolean workAlreadyRecorded(long empId, LocalDate date, String typeName) {
        return workRepository.existsByEmpIdAndStartAtDateAndTypeName(empId, date, typeName);
    }

    // 승인된 휴가/출장/재택 근무가 있는 지 체크
    private boolean hasApprovedWork(long empId, LocalDate date) {
        List<String> typeNames = List.of(
                WorkTypeName.REMOTE_WORK.name(),
                WorkTypeName.VACATION.name(),
                WorkTypeName.BUSINESS_TRIP.name()
        );

        List<Work> works = workRepository.findAllByEmpIdAndDateAndTypeNames(empId, date, typeNames);

        LocalDateTime startAt = LocalDateTime.of(date, getStartTime());
        LocalDateTime midTime = LocalDateTime.of(date, getMidTime());
        LocalDateTime endAt = LocalDateTime.of(date, getEndTime());

        for (Work foundWork : works) {
            int typeId = foundWork.getTypeId();
            WorkType type = workTypeRepository.findById(typeId)
                    .orElseThrow(() -> new WorkException(ErrorCode.WORKTYPE_NOT_FOUND));

            WorkTypeName typeName = type.getTypeName();

            if (typeName == WorkTypeName.REMOTE_WORK || typeName == WorkTypeName.BUSINESS_TRIP) {
                return true;
            }

            if (typeName == WorkTypeName.VACATION) {
                if (hasAMHalfDayOff(empId, date) || hasPMHalfDayOff(empId, date)) {
                    return true;
                }

                // 반차가 아닌 일반 휴가 중 겹치는 일정이 있는지 체크
                LocalDateTime vacationStart = foundWork.getStartAt();
                LocalDateTime vacationEnd = foundWork.getEndAt();

                boolean overlaps = vacationStart.isBefore(endAt) && vacationEnd.isAfter(startAt);

                if (overlaps) return true;

            }
        }

        return false;
    }

    private boolean hasHalfDayOff(long empId, LocalDate date, LocalTime start, LocalTime end) {
        return workRepository.findAllByEmpIdAndDateAndTypeNames(empId, date, List.of(WorkTypeName.VACATION.name()))
                .stream()
                .anyMatch(work -> work.getStartAt().toLocalTime().equals(start) &&
                        work.getEndAt().toLocalTime().equals(end));
    }

    private boolean hasAMHalfDayOff(long empId, LocalDate date) {
        return hasHalfDayOff(empId, date, getStartTime(), getMidTime());
    }

    private boolean hasPMHalfDayOff(long empId, LocalDate date) {
        return hasHalfDayOff(empId, date, getMidTime(), getEndTime());
    }



    // 휴일인지 체크
    private boolean isHoliday(LocalDate date) {
        if (date.getDayOfWeek() == DayOfWeek.SUNDAY || date.getDayOfWeek() == DayOfWeek.SATURDAY) {
            return true;
        }

        // 회사 휴일 테이블에 등록된 날짜이면 return true 추가 필요

        return false;
    }


    private LocalDateTime computeStartAt(LocalDateTime startPushedAt, boolean hasAMHalfDayOff) {
        if (hasAMHalfDayOff) {
            return getLaterOne(startPushedAt, LocalDate.now().atTime(getMidTime()));
        }
        return getLaterOne(startPushedAt, LocalDate.now().atTime(getStartTime()));
    }

    private LocalTime getStartTime() {
        return LocalTime.of(9, 0); // 회사 정보로 수정 필요
    }

    private LocalTime getMidTime() {
        return getStartTime().plusMinutes(4 * 60 + 30);
    }

    private LocalTime getEndTime() {
        return getStartTime().plusHours(DEFAULT_WORK_HOURS).plusHours(1);
        // 휴게시간 1시간 추가
    }

    private int getBreakTime(LocalDateTime startAt, LocalDateTime endAt) {
        long diff = Duration.between(startAt, endAt).toMinutes();
        if (diff >= BREAK_60M_THRESHOLD) { // 8시간 30분부터 30분 추가 부여 필요
            return 60;
        }
        if (diff >= BREAK_30M_THRESHOLD) { // 4시간 이상 체류 시 30분의 휴게 부여
            return 30;
        }
        return 0;
    }

    private LocalDateTime getEarlierOne(LocalDateTime first, LocalDateTime second) {
        return first.isBefore(second) ? first : second;
    }

    private LocalDateTime getLaterOne(LocalDateTime first, LocalDateTime second) {
        return first.isAfter(second) ? first : second;
    }


}