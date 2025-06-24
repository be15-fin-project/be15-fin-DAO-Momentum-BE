package com.dao.momentum.retention.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "면담 기록 목록 응답 DTO")
public class RetentionContactListResultDto {

    @Schema(description = "면담 기록 항목 목록")
    private List<RetentionContactItemDto> items;

    @Schema(description = "페이지네이션 정보")
    private Pagination pagination;
}
