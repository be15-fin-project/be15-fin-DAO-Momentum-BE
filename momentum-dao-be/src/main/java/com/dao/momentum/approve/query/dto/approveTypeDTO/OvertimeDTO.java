package com.dao.momentum.approve.query.dto.approveTypeDTO;

import lombok.*;

import java.time.LocalDateTime;

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
}
