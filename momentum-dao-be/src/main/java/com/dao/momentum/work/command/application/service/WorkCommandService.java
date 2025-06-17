package com.dao.momentum.work.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.company.command.domain.aggregate.IpAddress;
import com.dao.momentum.organization.company.command.domain.repository.IpAddressRepository;
import com.dao.momentum.work.command.application.dto.response.WorkEndResponse;
import com.dao.momentum.work.command.application.dto.response.WorkStartResponse;
import com.dao.momentum.work.command.application.dto.response.WorkSummaryDTO;
import com.dao.momentum.work.command.application.validator.IpValidator;
import com.dao.momentum.work.command.application.validator.WorkCreateValidator;
import com.dao.momentum.work.command.application.validator.WorkUpdateValidator;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkCommandService {

    private static final int MINUTES_IN_HOUR = 60;

    private final WorkTypeRepository workTypeRepository;
    private final WorkRepository workRepository;
    private final IpValidator ipValidator;
    private final WorkCreateValidator workCreateValidator;
    private final WorkTimeService workTimeService;
    private final IpAddressRepository ipAddressRepository;
    private final WorkUpdateValidator workUpdateValidator;

    @Transactional
    public WorkStartResponse createWork(UserDetails userDetails, HttpServletRequest httpServletRequest, LocalDateTime startPushedAt) {
        final String ip = extractClientIp(httpServletRequest);
        final long empId = Long.parseLong(userDetails.getUsername());

        log.info("출근 등록 요청 - 사원ID: {}, IP: {}, 요청 시각: {}", empId, ip, startPushedAt);

        ipValidator.validateIp(ip, getAllowedIps());

        final LocalDate today = startPushedAt.toLocalDate();

        final boolean hasAMHalfDayoff = workCreateValidator.hasAMHalfDayOff(empId, today);
        final boolean hasPMHalfDayoff = workCreateValidator.hasPMHalfDayOff(empId, today);

        final LocalDateTime startAt = workTimeService.computeStartAt(startPushedAt, hasAMHalfDayoff);
        final LocalTime midTime = workTimeService.getMidTime();
        final LocalTime endTime = workTimeService.getEndTime();

        final LocalDateTime endAt = hasPMHalfDayoff ?
                today.atTime(midTime) : today.atTime(endTime);

        workCreateValidator.validateWorkCreation(empId, today, startPushedAt, startAt, endAt);

        final WorkType workType = getWorkType(WorkTypeName.WORK);

        final int typeId = workType.getTypeId();

        final int breakTime = workTimeService.getBreakTime(startAt, endAt);

        final Work work = Work.builder()
                .empId(empId)
                .typeId(typeId)
                .startAt(startAt)
                .endAt(endAt)
                .startPushedAt(startPushedAt)
                .breakTime(breakTime)
                .build();

        int requiredMinutes = WorkTimeService.DEFAULT_WORK_HOURS * MINUTES_IN_HOUR;
        if (hasAMHalfDayoff || hasPMHalfDayoff) {
            requiredMinutes /= 2;
        }

        final IsNormalWork isNormalWork = work.isNormalWork(requiredMinutes) ?
                IsNormalWork.Y : IsNormalWork.N;
        work.setIsNormalWork(isNormalWork);


        workRepository.save(work);
        final WorkSummaryDTO workSummaryDTO = WorkSummaryDTO.from(work);
        log.info("출근 등록 완료 - workId: {}, empId: {}, 등록일시: {}", work.getWorkId(), empId, startPushedAt);


        return WorkStartResponse.builder()
                .workSummaryDTO(workSummaryDTO)
                .message("출근 등록 성공")
                .build();
    }

    @Transactional
    public WorkEndResponse updateWork(UserDetails userDetails, HttpServletRequest httpServletRequest, LocalDateTime endPushedAt) {
        final String ip = extractClientIp(httpServletRequest);
        final long empId = Long.parseLong(userDetails.getUsername());

        log.info("퇴근 등록 요청 - 사원ID: {}, IP: {}, 요청 시각: {}", empId, ip, endPushedAt);
        ipValidator.validateIp(ip, getAllowedIps());

        final LocalDate today = endPushedAt.toLocalDate();

        final WorkType workType = getWorkType(WorkTypeName.WORK);

        // 기존 출근 기록 조회
        final Work work = workRepository.findByEmpIdAndDateAndTypeName(empId, today, workType.getTypeName())
                .orElseThrow(() -> new WorkException(ErrorCode.WORK_NOT_FOUND));

        final boolean hasAMHalfDayoff = workUpdateValidator.hasAMHalfDayOff(empId, today);
        final boolean hasPMHalfDayoff = workUpdateValidator.hasPMHalfDayOff(empId, today);

        final LocalDateTime endAt = workTimeService.computeEndAt(endPushedAt, hasPMHalfDayoff);

        final LocalDateTime startAt = work.getStartAt();
        workUpdateValidator.validateWorkUpdate(today, endPushedAt, startAt, endAt);

        final int breakTime = workTimeService.getBreakTime(work.getStartAt(), endAt);

        int requiredMinutes = WorkTimeService.DEFAULT_WORK_HOURS * MINUTES_IN_HOUR;
        if (hasAMHalfDayoff || hasPMHalfDayoff) {
            requiredMinutes /= 2;
        }

        work.fromUpdate(endAt, endPushedAt, breakTime);

        final IsNormalWork isNormalWork = work.isNormalWork(requiredMinutes) ?
                IsNormalWork.Y : IsNormalWork.N;
        work.setIsNormalWork(isNormalWork);

        final WorkSummaryDTO workSummaryDTO = WorkSummaryDTO.from(work);

        return WorkEndResponse.builder()
                .workSummaryDTO(workSummaryDTO)
                .message("퇴근 등록 성공")
                .build();
    }

    private WorkType getWorkType(WorkTypeName workTypeName) {
        return workTypeRepository.findByTypeName(workTypeName)
                .orElseThrow(() -> {
                    log.error("WorkType '{}'을(를) 찾을 수 없음", workTypeName);
                    return new WorkException(ErrorCode.WORKTYPE_NOT_FOUND);
                });
    }

    private List<String> getAllowedIps() {
        return ipAddressRepository.findAll().stream()
                .map(IpAddress::getIpAddress)
                .toList();

//        return List.of("0.0.0.0/0", "::/0");
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
                String clientIp = ip.split(",")[0].trim();
                if (isLoopbackAddress(clientIp)) {
                    return clientIp;  // 그대로 반환하거나 "127.0.0.1"로 통일해도 됨
                }
                return clientIp;
            }
        }

        return request.getRemoteAddr();
    }

    private boolean isLoopbackAddress(String ip) {
        return "127.0.0.1".equals(ip) || "::1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip);
    }

}