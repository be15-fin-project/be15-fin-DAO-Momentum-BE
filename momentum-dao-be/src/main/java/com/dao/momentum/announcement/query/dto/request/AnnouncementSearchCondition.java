package com.dao.momentum.announcement.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "공지사항 검색 조건 내부 DTO (서비스 내부 변환용)")
public class AnnouncementSearchCondition {

    @Schema(description = "공지사항 제목", example = "시스템 점검")
    private String title;

    @Schema(description = "작성자 이름", example = "홍길동")
    private String name;

    @Schema(description = "부서 ID", example = "1")
    private Integer deptId;

    @Schema(description = "조회 시작일", example = "2025-07-01")
    private LocalDate startDate;

    @Schema(description = "조회 종료일 (포함)", example = "2025-07-08")
    private LocalDate endDate;

    @Schema(description = "정렬 방향", example = "DESC")
    private SortDirection sortDirection;

    @Schema(description = "페이지 오프셋", example = "0")
    private int offset;

    @Schema(description = "페이지당 조회 개수", example = "10")
    private int limit;

    public static AnnouncementSearchCondition from(AnnouncementSearchRequest request) {
        return AnnouncementSearchCondition.builder()
                .title(request.getTitle())
                .name(request.getName())
                .deptId(request.getDeptId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate() != null ? request.getEndDate().plusDays(1) : null)
                .sortDirection(request.getSortDirection())
                .offset(request.getOffset())
                .limit(request.getLimit())
                .build();
    }
}