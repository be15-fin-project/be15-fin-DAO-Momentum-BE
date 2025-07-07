package com.dao.momentum.announcement.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "공지사항 목록 응답")
public class AnnouncementListResponse {

    @Schema(description = "공지사항 목록")
    private final List<AnnouncementDto> announcements;

    @Schema(description = "페이지네이션 정보")
    private final Pagination pagination;
}
