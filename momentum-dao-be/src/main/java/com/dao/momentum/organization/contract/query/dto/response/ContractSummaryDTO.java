package com.dao.momentum.organization.contract.query.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ContractSummaryDTO {
    private Long contractId;

    private Long empId;

    private Long empNo;

    private ContractType type;

    private String empName;

    private BigDecimal salary;

    private LocalDateTime createdAt;
}
