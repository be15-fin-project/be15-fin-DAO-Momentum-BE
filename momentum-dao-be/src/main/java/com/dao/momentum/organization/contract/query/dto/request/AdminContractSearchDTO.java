package com.dao.momentum.organization.contract.query.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AdminContractSearchDTO {
    private String empNo;

    private String empName;

    private ContractType type;

    private LocalDate searchStartDate;

    private LocalDate searchEndDate;

    private Order order;

    @Min(1)
    private Integer page;

    @Min(1)
    private Integer size;

    public int getOffset() {
        return (page - 1) * size;
    }

    public int getLimit() {
        return size;
    }
}
