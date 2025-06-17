package com.dao.momentum.announcement.command.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AnnouncementModifyResponse {
    private Long announcementId;
}
