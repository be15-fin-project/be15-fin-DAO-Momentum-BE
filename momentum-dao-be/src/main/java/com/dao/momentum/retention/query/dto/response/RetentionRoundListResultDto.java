package com.dao.momentum.retention.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "근속 전망 회차 목록 조회 결과")
public class RetentionRoundListResultDto {

    @Schema(description = "회차 목록")
    private List<RetentionRoundListResponseDto> content;

    @Schema(description = "페이지네이션 정보")
    private Pagination pagination;
}
