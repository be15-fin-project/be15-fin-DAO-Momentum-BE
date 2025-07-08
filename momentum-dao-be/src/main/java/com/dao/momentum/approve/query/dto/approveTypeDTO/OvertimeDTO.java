package com.dao.momentum.approve.query.dto.approveTypeDTO;

import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OvertimeDTO {

    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private int breakTime;
    private String reason;

    private List<String> workTypes = new ArrayList<>();

}
