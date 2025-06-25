package com.dao.momentum.work.query.dto.response;

import com.dao.momentum.work.command.domain.aggregate.IsNormalWork;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WorkDTO {
    private long workId;

    private long empId;

    private long empNo;

    private String empName;

    private int typeId;

    private String typeName;

    private Integer vacationTypeId;

    private String vacationType;

    @NotNull
    private LocalDateTime startAt;
    @NotNull
    private LocalDateTime endAt;

    private IsNormalWork isNormalWork;

}
