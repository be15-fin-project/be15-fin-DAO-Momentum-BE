package com.dao.momentum.announcement.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "공지사항 수정 응답")
public class AnnouncementModifyResponse {
    @Schema(description = "수정된 공지사항 ID")
    private Long announcementId;
}
