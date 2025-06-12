package com.dao.momentum.work.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.work.command.application.dto.request.WorkStartRequest;
import com.dao.momentum.work.command.application.dto.response.WorkCreateResponse;
import com.dao.momentum.work.command.application.dto.response.WorkSummaryDTO;
import com.dao.momentum.work.command.domain.aggregate.IsNormalWork;
import com.dao.momentum.work.command.domain.aggregate.Work;
import com.dao.momentum.work.command.domain.aggregate.WorkType;
import com.dao.momentum.work.command.domain.repository.WorkRepository;
import com.dao.momentum.work.command.domain.repository.WorkTypeRepository;
import com.dao.momentum.work.exception.WorkException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class WorkCommandService {
    private static final int DEFAULT_WORK_HOURS = 9;

    private final WorkTypeRepository workTypeRepository;
    private final WorkRepository workRepository;

    public WorkCreateResponse createWork(WorkStartRequest workStartRequest, HttpServletRequest httpServletRequest) {
        long empId = 1; // 로그인 구현 후 수정
        WorkType workType = workTypeRepository.findByTypeName("WORK")
                .orElseThrow(() -> new WorkException(ErrorCode.WORKTYPE_NOT_FOUND));

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

        IsNormalWork isNormalWork = work.getWorkTime().toHours() >= 8? IsNormalWork.Y : IsNormalWork.N;
        work.setIsNormalWork(isNormalWork);

        WorkSummaryDTO workSummaryDTO = WorkSummaryDTO.from(work);

        workRepository.save(work);

        return WorkCreateResponse.builder()
                .workSummaryDTO(workSummaryDTO)
                .message("출근 등록 성공")
                .build();
    }

    private LocalDateTime computeStartAt(LocalDateTime startPushedAt) {
        return getLaterOne(startPushedAt, LocalDate.now().atTime(getStartTime()));
    }

    private LocalTime getStartTime() {
        return LocalTime.of(9, 0); // 회사 정보로 수정 필요
    }

    private LocalTime getEndTime() {
        return getStartTime().plusHours(DEFAULT_WORK_HOURS);
    }

    private LocalTime getMidTime() {
        return getStartTime().plusMinutes((long) 4.5 * 60);
    }

    private int getBreakTime(LocalDateTime startAt, LocalDateTime endAt) {
        long diff = Duration.between(startAt, endAt).toMinutes();
        if (diff >= 8.5 * 60) { // 8시간 30분부터 30분 추가 부여 필요
            return 60;
        }
        if (diff >= 4 * 60) { // 4시간 이상 체류 시 30분의 휴게 부여
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