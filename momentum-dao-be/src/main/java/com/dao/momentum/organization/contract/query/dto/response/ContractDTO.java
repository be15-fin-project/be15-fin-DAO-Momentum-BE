package com.dao.momentum.organization.contract.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "본인 계약서 응답 DTO")
public class ContractDTO {
    @Schema(description = "계약서 번호", example = "1")
    private Long contractId;

    @Schema(description = "계약자 ID", example = "2")
    private Long empId;

    @Schema(description = "계약 유형")
    private ContractType type;

    @Schema(description = "계약 연봉", example = "40000000")
    private BigDecimal salary;

    @Schema(description = "등록 일시")
    private LocalDateTime createdAt;

    @Schema(description = "첨부파일 ID", example = "1")
    private Long attachmentId;

    @Schema(description = "다운로드 키")
    private String s3Key;
}
