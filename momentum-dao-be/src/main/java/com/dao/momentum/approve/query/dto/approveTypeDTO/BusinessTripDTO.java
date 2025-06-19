package com.dao.momentum.approve.query.dto.approveTypeDTO;

import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessTripDTO {

    private String type;
    private String place;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private int cost;

}
