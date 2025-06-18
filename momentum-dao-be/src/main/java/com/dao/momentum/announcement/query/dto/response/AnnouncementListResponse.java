package com.dao.momentum.announcement.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AnnouncementListResponse {
    private final List<AnnouncementDto> announcements;
    private final Pagination pagination;
}
