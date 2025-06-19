package com.dao.momentum.approve.query.dto;

import com.dao.momentum.approve.command.domain.aggregate.IsRequiredAll;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveLineDTO {

    private Long approveLineId;
    private String statusType;
    private Long approveId;
    private Integer approveLineOrder;
    private IsRequiredAll isRequiredAll;

}
