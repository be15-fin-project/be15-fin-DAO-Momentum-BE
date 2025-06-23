package com.dao.momentum.organization.contract.command.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContractDeleteResponse {
    private long contractId;

    private String message;
}
