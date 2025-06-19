package com.dao.momentum.approve.query.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveLineListDTO {

    private Long approveLineId;
    private String statusType;
    private String employeeDisplayName;
    private String departmentName;
    private String reason;

}
