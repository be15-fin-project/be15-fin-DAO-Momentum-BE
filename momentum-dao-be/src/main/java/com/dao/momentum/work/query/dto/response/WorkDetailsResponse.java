package com.dao.momentum.work.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "근무 상세 정보 응답 객체")
public class WorkDetailsResponse {
    @Schema(description = "근무 상세 내용")
    private WorkDetailsDTO workDetails;
}
