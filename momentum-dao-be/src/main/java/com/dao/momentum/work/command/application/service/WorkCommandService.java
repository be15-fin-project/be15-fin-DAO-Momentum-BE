package com.dao.momentum.work.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.work.command.application.dto.request.WorkStartRequest;
import com.dao.momentum.work.command.application.dto.response.WorkStartResponse;
import com.dao.momentum.work.command.application.dto.response.WorkSummaryDTO;
import com.dao.momentum.work.command.application.validator.IpValidator;
import com.dao.momentum.work.command.application.validator.WorkCreateValidator;
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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkCommandService {

    private final WorkTypeRepository workTypeRepository;
    private final WorkRepository workRepository;
    private final IpValidator ipValidator;
    private final WorkCreateValidator workCreateValidator;
    private final WorkTimeService workTimeService;

    public WorkStartResponse createWork(WorkStartRequest workStartRequest, HttpServletRequest httpServletRequest) {
        String ip = extractClientIp(httpServletRequest);
        LocalDateTime startPushedAt = workStartRequest.getStartPushedAt();
        log.info("출근 등록 요청 - IP: {}, 요청 시각: {}", ip, startPushedAt);

        ipValidator.validateIp(ip, getAllowedIps());

        long empId = 1; // 로그인 구현 후 수정
        LocalDate today = LocalDate.now();

        boolean hasAMHalfDayoff = workCreateValidator.hasAMHalfDayOff(empId, today);
        boolean hasPMHalfDayoff = workCreateValidator.hasPMHalfDayOff(empId, today);

        LocalDateTime startAt = workTimeService.computeStartAt(startPushedAt, hasAMHalfDayoff);
        LocalTime midTime = workTimeService.getMidTime();
        LocalTime endTime = workTimeService.getEndTime();

        LocalDateTime endAt = hasPMHalfDayoff ?
                today.atTime(midTime) : today.atTime(endTime);

        workCreateValidator.validateWorkCreation(empId, today, startPushedAt, startAt, endAt);

        WorkType workType = workTypeRepository.findByTypeName(WorkTypeName.WORK)
                .orElseThrow(() -> {
                    log.error("WorkType '{}'을(를) 찾을 수 없음", WorkTypeName.WORK);
                    return new WorkException(ErrorCode.WORKTYPE_NOT_FOUND);
                });

        int typeId = workType.getTypeId();

        int breakTime = workTimeService.getBreakTime(startAt, endAt);

        Work work = Work.builder()
                .empId(empId)
                .typeId(typeId)
                .startAt(startAt)
                .endAt(endAt)
                .startPushedAt(startPushedAt)
                .breakTime(breakTime)
                .build();

        int requiredMinutes = WorkTimeService.DEFAULT_WORK_HOURS;
        if (hasAMHalfDayoff || hasPMHalfDayoff) {
            requiredMinutes /= 2;
        }

        IsNormalWork isNormalWork = work.isNormalWork(requiredMinutes) ?
                IsNormalWork.Y : IsNormalWork.N;
        work.setIsNormalWork(isNormalWork);

        WorkSummaryDTO workSummaryDTO = WorkSummaryDTO.from(work);

        workRepository.save(work);
        log.info("출근 등록 완료 - workId: {}, empId: {}, 등록일시: {}", work.getWorkId(), empId, startPushedAt);


        return WorkStartResponse.builder()
                .workSummaryDTO(workSummaryDTO)
                .message("출근 등록 성공")
                .build();
    }

    private List<String> getAllowedIps() {
//        return List.of();
        return List.of("0.0.0.0/0");
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



}