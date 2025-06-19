package com.dao.momentum.approve.query.dto.approveTypeDTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkCorrectionDTO {

    private LocalDateTime beforeStartAt;
    private LocalDateTime beforeEndAt;
    private LocalDateTime afterStartAt;
    private LocalDateTime afterEndAt;
    private String reason;

}
