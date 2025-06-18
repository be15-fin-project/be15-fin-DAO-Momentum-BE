package com.dao.momentum.announcement.query.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AnnouncementDto {
    private final Long announcementId;
    private final String title;
    private final String name;
    private final LocalDateTime createdAt;
}

