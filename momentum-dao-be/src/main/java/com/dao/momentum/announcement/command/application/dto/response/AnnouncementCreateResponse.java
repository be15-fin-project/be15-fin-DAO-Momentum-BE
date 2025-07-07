package com.dao.momentum.announcement.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "공지사항 생성 응답")
public class AnnouncementCreateResponse {
    @Schema(description = "생성된 공지사항 ID")
    private Long announcementId;
}
