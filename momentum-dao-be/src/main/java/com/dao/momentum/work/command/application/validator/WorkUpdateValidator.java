package com.dao.momentum.work.command.application.validator;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.work.command.application.service.WorkTimeService;
import com.dao.momentum.work.command.domain.repository.WorkRepository;
import com.dao.momentum.work.exception.WorkException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
public class WorkUpdateValidator extends WorkCommandValidator {

    public WorkUpdateValidator(
            WorkRepository workRepository,
            WorkTimeService workTimeService
    ) {
        super(workRepository, workTimeService); // 부모 클래스의 생성자 호출
    }

    public void validateWorkUpdate(LocalDate today, LocalDateTime endPushedAt, LocalDateTime startAt, LocalDateTime endAt) {

        validateEndAt(startAt, endAt, endPushedAt, today);
    }

    public void validateEndAt(LocalDateTime startAt, LocalDateTime endAt, LocalDateTime endPushedAt, LocalDate today) {
        LocalDate pushedDate = endPushedAt.toLocalDate();

        if (!pushedDate.isEqual(today)) {
            log.warn("잘못된 퇴근 요청 - 퇴근 시각이 오늘 날짜가 아님: {}", startAt.toLocalDate());
            throw new WorkException(ErrorCode.INVALID_WORK_TIME);
        }

        if (!startAt.isBefore(endAt)) {
            log.warn("잘못된 퇴근 요청 - 출근 시각이 종료 시각보다 늦거나 같음: startAt={}, endAt={}", startAt, endAt);
            throw new WorkException(ErrorCode.INVALID_WORK_TIME);
        }
    }


}
