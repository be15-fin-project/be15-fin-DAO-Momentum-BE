package com.dao.momentum.organization.contract.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ContractListResponse {
    private List<ContractSummaryDTO> contracts;

    private Pagination pagination;
}
