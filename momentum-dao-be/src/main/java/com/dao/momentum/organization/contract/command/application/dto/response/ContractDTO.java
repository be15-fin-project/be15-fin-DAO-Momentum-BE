package com.dao.momentum.organization.contract.command.application.dto.response;

import com.dao.momentum.organization.contract.command.domain.aggregate.ContractType;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ContractDTO {
    private long contractId;

    private long empId;

    private ContractType type;

    @Column(precision = 15, scale = 3)
    private BigDecimal salary;

    private LocalDateTime createdAt;
}
