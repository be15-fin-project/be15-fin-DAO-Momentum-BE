package com.dao.momentum.organization.contract.command.application.dto.request;

import com.dao.momentum.file.command.application.dto.request.AttachmentRequest;
import com.dao.momentum.organization.contract.command.domain.aggregate.ContractType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@Schema(description = "계약서 등록 요청 객체")
public class ContractCreateRequest {
    @Schema(description = "계약 대상자 ID", example = "1")
    private long empId;

    @NotNull
    @Schema(description = "계약 유형")
    private ContractType type;

    @Column(precision = 15, scale = 3)
    @Schema(description = "계약 연봉", example = "40000000")
    private BigDecimal salary;

    @Valid
    @Schema(description = "첨부파일")
    private AttachmentRequest attachment; // S3에 미리 업로드된 파일 정보 전달 (리스트일 필요는 없을 듯)

}
