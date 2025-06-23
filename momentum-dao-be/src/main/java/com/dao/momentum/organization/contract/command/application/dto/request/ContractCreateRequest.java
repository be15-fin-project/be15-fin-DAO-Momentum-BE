package com.dao.momentum.organization.contract.command.application.dto.request;

import com.dao.momentum.file.command.application.dto.request.AttachmentRequest;
import com.dao.momentum.organization.contract.command.domain.aggregate.ContractType;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ContractCreateRequest {
    private long empId;

    private ContractType type;

    @Column(precision = 15, scale = 3)
    private BigDecimal salary;

    @Valid
    private AttachmentRequest attachment; // S3에 미리 업로드된 파일 정보 전달 (리스트일 필요는 없을 듯)

}
