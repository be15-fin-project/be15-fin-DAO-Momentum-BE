package com.dao.momentum.organization.contract.command.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContractCreateResponse {
    private long contractId;

    private String message;
}
