package com.dao.momentum.work.query.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AttendanceDTO {
    private Long workId;

    private LocalDateTime startAt;

    private LocalDateTime endAt;
}
