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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
        String ip = httpServletRequest.getRemoteAddr();
        log.info("출근 등록 요청 - IP: {}, 요청 시각: {}", ip, workStartRequest.getStartPushedAt());

        List<String> AllowedIps = getAllowedIps();
        if (!isvalidIp(ip, AllowedIps)) {
            log.warn("허용되지 않은 IP 접근 시도: {}", ip);
            throw new WorkException(ErrorCode.IP_NOT_ALLOWED);
        }

        long empId = 1; // 로그인 구현 후 수정
        WorkType workType = workTypeRepository.findByTypeName(WorkTypeName.WORK.name())
                .orElseThrow(() -> {
                    log.error("WorkType '{}'을(를) 찾을 수 없음", WorkTypeName.WORK.name());
                    return new WorkException(ErrorCode.WORKTYPE_NOT_FOUND);
                });

        int typeId = workType.getTypeId();

        LocalDateTime startPushedAt = workStartRequest.getStartPushedAt();
        LocalDateTime startAt = computeStartAt(startPushedAt);
        LocalDateTime endAt = LocalDateTime.of(LocalDate.now(), getEndTime());

        int breakTime = getBreakTime(startAt, endAt);

        Work work = Work.builder()
                .empId(empId)
                .typeId(typeId)
                .startAt(startAt)
                .endAt(endAt)
                .startPushedAt(startPushedAt)
                .breakTime(breakTime)
                .build();

        IsNormalWork isNormalWork = work.getWorkTime().toHours() >= DEFAULT_WORK_HOURS? IsNormalWork.Y : IsNormalWork.N;
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

    private boolean isvalidIp(String ip, List<String> allowedIps) {
        for (String allowedIp : allowedIps) {
            try {
                if (allowedIp.contains("/")) {
                    SubnetUtils subnet = new SubnetUtils(allowedIp);
                    subnet.setInclusiveHostCount(true);
                    if (subnet.getInfo().isInRange(ip)) {
                        return true;
                    }
                } else {
                    if (ip.equals(allowedIp)) {
                        return true;
                    }
                }
            } catch (IllegalArgumentException e) {
                log.warn("잘못된 IP: {}", allowedIp, e);
            }
        }
        return false;
    }

    private LocalDateTime computeStartAt(LocalDateTime startPushedAt) {
        return getLaterOne(startPushedAt, LocalDate.now().atTime(getStartTime()));
    }

    private LocalTime getStartTime() {
        return LocalTime.of(9, 0); // 회사 정보로 수정 필요
    }

    private LocalTime getEndTime() {
        return getStartTime().plusHours(DEFAULT_WORK_HOURS).plusHours(1);
        // 휴게시간 1시간 추가
    }

    private LocalTime getMidTime() {
        return getStartTime().plusMinutes((long) 4.5 * 60);
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
        return first.isBefore(second)? first : second;
    }

    private LocalDateTime getLaterOne(LocalDateTime first, LocalDateTime second) {
        return first.isAfter(second)? first : second;
    }


}