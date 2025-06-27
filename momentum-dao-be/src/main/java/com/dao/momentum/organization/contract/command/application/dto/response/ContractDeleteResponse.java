package com.dao.momentum.organization.contract.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "계약서 삭제 응답 객체")
public class ContractDeleteResponse {
    @Schema(description = "계약서 ID", example = "1")
    private long contractId;

    @Schema(description = "응답 메시지", example = "계약서 삭제 완료")
    private String message;
}
