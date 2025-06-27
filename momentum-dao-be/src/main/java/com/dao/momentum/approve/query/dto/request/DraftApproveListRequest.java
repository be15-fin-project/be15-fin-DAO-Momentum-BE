package com.dao.momentum.approve.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
@Builder
@Schema(description = "결재 목록 request")
public class DraftApproveListRequest {

    @NotNull(message = "결재 종류는 반드시 선택해야 합니다.")
    @Schema(description = "탭 구분 값")
    private final String tab;

    @Schema(description = "결재 문서 종류")
    private final String approveType;

    @Schema(description = "영수증 종류")
    private final String receiptType;

    @Schema(description = "결재 상태", example = "1")
    private final Integer status;

    @Schema(description = "결재 제목 필터링을 위한 검색어")
    private final String title;

    @Schema(description = "검색 기간의 시작일")
    private final LocalDate startDate;

    @Schema(description = "검색 기간의 종료일")
    private final LocalDate endDate;

    @Schema(description = "정렬 방식 지정(오름차순/내림차순)")
    private final String sort;

}