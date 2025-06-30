package com.dao.momentum.organization.employee.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "인사 발령 내역 조회 응답 객체")
public class AppointListResponse {
    @Schema(description = "인사 발령 내역")
    private List<AppointDTO> appoints;

    @Schema(description = "페이지네이션 정보")
    private Pagination pagination;
}
