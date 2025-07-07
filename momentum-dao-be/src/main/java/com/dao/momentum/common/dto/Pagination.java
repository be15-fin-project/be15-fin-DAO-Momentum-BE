package com.dao.momentum.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "페이지네이션 정보")
public class Pagination {

    @Schema(description = "현재 페이지", example = "1")
    private final int currentPage;

    @Schema(description = "전체 페이지 수", example = "10")
    private final int totalPage;

    @Schema(description = "전체 항목 수", example = "100")
    private final long totalItems;
}