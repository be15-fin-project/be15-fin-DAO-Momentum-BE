package com.dao.momentum.announcement.query.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AnnouncementDetailResponse {
    private final AnnouncementDetailDto announcement;
}
