package com.dao.momentum.work.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "근무 내역 응답 객체")
public class WorkListResponse {
    @Schema(description = "근무 내역")
    private List<WorkDTO> works;

    @Schema(description = "페이지네이션 정보")
    private Pagination pagination;
}
