package com.dao.momentum.approve.query.dto.approveTypeDTO;

import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemoteWorkDTO {

    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;

}
