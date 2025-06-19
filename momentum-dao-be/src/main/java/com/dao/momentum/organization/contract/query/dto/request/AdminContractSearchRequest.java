package com.dao.momentum.organization.contract.query.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AdminContractSearchRequest {
    private Long empNo;

    private String empName;

    private ContractType type;

    private LocalDate searchStartDate;

    private LocalDate searchEndDate;

    private Order order;

    @Min(1)
    private Integer page;

    @Min(1)
    private Integer size;

    public static AdminContractSearchDTO fromRequest(AdminContractSearchRequest request) {
        return AdminContractSearchDTO.builder()
                .empNo(request.getEmpNo())
                .empName(request.getEmpName())
                .type(request.getType())
                .searchStartDate(request.getSearchStartDate())
                .searchEndDate(request.getSearchEndDate())
                .order(request.getOrder())
                .page(request.getPage() == null ? 1 : request.getPage())
                .size(request.getSize() == null ? 10 : request.getSize())
                .build();
    }
}
