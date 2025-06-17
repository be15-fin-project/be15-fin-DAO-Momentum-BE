package com.dao.momentum.approve.query.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DraftApproveDTO {

    private Long approveId;
    private Long parentApproveId;
    private String statusType;
    private String approveTitle;
    private String approveType;
    private LocalDateTime createAt;
    private LocalDateTime completeAt;

}
