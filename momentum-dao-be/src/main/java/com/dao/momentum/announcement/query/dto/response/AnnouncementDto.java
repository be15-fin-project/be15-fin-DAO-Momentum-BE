package com.dao.momentum.announcement.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "공지사항 목록 DTO")
public class AnnouncementDto {

    @Schema(description = "공지사항 ID", example = "1")
    private final Long announcementId;

    @Schema(description = "공지사항 제목", example = "정기 점검 안내")
    private final String title;

    @Schema(description = "작성자 이름", example = "홍길동")
    private final String name;

    @Schema(description = "작성일시", example = "2025-07-07T09:00:00")
    private final LocalDateTime createdAt;
}

