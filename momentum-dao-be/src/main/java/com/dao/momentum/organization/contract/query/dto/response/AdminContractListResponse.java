package com.dao.momentum.organization.contract.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "계약서 목록 응답 객체")
public class AdminContractListResponse {
    @Schema(description = "계약서 목록")
    private List<AdminContractDTO> contracts;

    @Schema(description = "페이지네이션 정보")
    private Pagination pagination;
}
