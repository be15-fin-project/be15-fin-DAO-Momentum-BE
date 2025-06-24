package com.dao.momentum.retention.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "근속 전망 상세 조회 요청 DTO")
public class RetentionSupportDetailRequestDto {

    @Schema(description = "회차 ID", example = "12", required = true)
    private Integer roundId;

    @Schema(description = "사원 ID", example = "10023", required = true)
    private Long empId;
}
