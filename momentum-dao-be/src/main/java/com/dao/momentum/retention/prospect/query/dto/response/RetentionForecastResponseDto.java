package com.dao.momentum.retention.prospect.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "근속 전망 조회 응답 DTO")
public class RetentionForecastResponseDto {

    @Schema(description = "근속 전망 항목 목록")
    private List<RetentionForecastItemDto> items;

    @Schema(description = "페이지네이션 정보")
    private Pagination pagination;
}
