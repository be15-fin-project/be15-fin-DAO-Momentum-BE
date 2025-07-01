package com.dao.momentum.approve.query.dto.request;

import com.dao.momentum.approve.command.domain.aggregate.ApproveType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
@Builder
@Schema(description = "결재 목록 request")
public class ApproveListRequest {

    @NotNull(message = "결재 종류는 반드시 선택해야 합니다.")
    @Schema(description = "탭 구분")
    private final String tab;

    @Schema(description = "결재 문서 종류")
    private final ApproveType approveType;

    @Schema(description = "영수증 종류")
    private final String receiptType;

    @Schema(description = "결재 상태 ID", example = "1")
    private final Integer status;

    @Schema(description = "결재 제목 검색어")
    private final String title;

    @Schema(description = "기안자 이름 검색어")
    private final String employeeName;

    @Schema(description = "기안자 소속 부서 아이디", example = "1")
    private final Long deptId;

    @Schema(description = "검색 시작일")
    private final LocalDate startDate;

    @Schema(description = "검색 종료일")
    private final LocalDate endDate;

    @Schema(description = "정렬 방식")
    private final String sort;

}
