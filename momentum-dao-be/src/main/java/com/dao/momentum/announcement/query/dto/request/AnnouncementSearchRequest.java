package com.dao.momentum.announcement.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "공지사항 검색 요청 파라미터")
public class AnnouncementSearchRequest {

    @Size(min = 1, message = "검색할 제목은 1자 이상 입력해주세요.")
    @Schema(description = "공지사항 제목 검색어", example = "시스템 점검")
    private String title;

    @Size(min = 1, message = "검색할 작성자 이름은 1글자 이상입니다.")
    @Schema(description = "작성자 이름 검색어", example = "홍길동")
    private String name;

    @Min(value = 1, message = "검색할 부서 ID는 1 이상이어야 합니다.")
    @Schema(description = "부서 ID", example = "1")
    private Integer deptId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "조회 시작일", example = "2025-07-01")
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "조회 종료일", example = "2025-07-07")
    private LocalDate endDate;

    @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
    @Schema(description = "페이지 번호 (1부터 시작)", example = "1")
    private Integer page = 1;

    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    @Schema(description = "페이지당 항목 수", example = "10")
    private Integer size = 10;

    @Schema(description = "정렬 방향 (ASC 또는 DESC)", example = "DESC")
    private SortDirection sortDirection = SortDirection.DESC;

    public int getOffset() {
        return (page - 1) * size;
    }

    public int getLimit() {
        return size;
    }

    public void validateDateRange() {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작일은 종료일보다 이후일 수 없습니다.");
        }
    }
}
