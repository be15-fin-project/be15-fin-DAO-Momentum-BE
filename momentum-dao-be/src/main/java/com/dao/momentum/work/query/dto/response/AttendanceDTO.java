package com.dao.momentum.work.query.dto.response;

import lombok.Getter;

@Getter
public class AttendanceDTO {
    private IsAttended isAttended;

    private Long empId;

    private Long workId;
}
