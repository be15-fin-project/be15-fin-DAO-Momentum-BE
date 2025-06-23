package com.dao.momentum.organization.contract.query.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ContractDTO {
    private Long contractId;

    private Long empId;

    private ContractType type;

    private BigDecimal salary;

    private LocalDateTime createdAt;

    private Long attachmentId;

    private String url;
}
