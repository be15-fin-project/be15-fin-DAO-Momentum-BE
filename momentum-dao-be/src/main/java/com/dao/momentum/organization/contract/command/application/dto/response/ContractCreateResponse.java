package com.dao.momentum.organization.contract.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "계약서 등록 응답 객체")
public class ContractCreateResponse {
    @Schema(description = "계약서 번호", example = "1")
    private long contractId;

    @Schema(description = "응답 메시지", example = "계약서 등록 완료")
    private String message;
}
