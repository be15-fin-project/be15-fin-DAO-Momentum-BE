package com.dao.momentum.organization.contract.query.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContractSearchRequest {
    private ContractType type;

//    private LocalDate searchStartDate;
//
//    private LocalDate searchEndDate;

    @Min(1)
    private Integer page;

    @Min(1)
    private Integer size;

    public static ContractSearchDTO fromRequest(ContractSearchRequest request) {
        return ContractSearchDTO.builder()
                .type(request.getType())
//                .searchStartDate(request.getSearchStartDate())
//                .searchEndDate(request.getSearchEndDate())
                .page(request.getPage() == null ? 1 : request.getPage())
                .size(request.getSize() == null ? 10 : request.getSize())
                .build();
    }
}
