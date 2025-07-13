package com.dao.momentum.evaluation.hr.query.dto.request;

import com.dao.momentum.common.dto.UseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "사원이 조회하는 본인 인사 평가 이의제기 목록 요청 DTO")
public record MyObjectionListRequestDto(
        @Schema(description = "상태 ID 필터 (1=대기,2=승인,3=반려 등)", example = "1")
        Integer statusId,

        @Schema(description = "평가 회차 ID", example = "10023")
        Integer roundId,

        @Schema(description = "삭제 여부 (Y, N)", example = "Y")
        UseStatus isDeleted,

        @Schema(description = "페이지 번호 (1부터 시작)", example = "1", defaultValue = "1")
        Integer page,

        @Schema(description = "페이지당 항목 수", example = "10", defaultValue = "10")
        Integer size
) {
    // 기본값 처리
    public MyObjectionListRequestDto {
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
    }

    public int getOffset() {
        return (page - 1) * size;
    }
}
