package com.dao.momentum.evaluation.hr.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "사원이 조회하는 본인 인사 평가 이의제기 목록 요청 DTO")
public class MyObjectionListRequestDto {

    @Schema(description = "상태 ID 필터 (1=대기,2=승인,3=반려 등)", example = "1")
    private Integer statusId;

    @Schema(description = "페이지 번호 (1부터 시작)", example = "1", defaultValue = "1")
    private int page = 1;

    @Schema(description = "페이지당 항목 수", example = "10", defaultValue = "10")
    private int size = 10;

    public int getOffset() {
        return (page - 1) * size;
    }
}
