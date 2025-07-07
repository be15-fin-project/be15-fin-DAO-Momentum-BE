package com.dao.momentum.announcement.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "공지사항 상세 응답")
public class AnnouncementDetailResponse {

    @Schema(description = "공지사항 상세 정보")
    private final AnnouncementDetailDto announcement;
}
