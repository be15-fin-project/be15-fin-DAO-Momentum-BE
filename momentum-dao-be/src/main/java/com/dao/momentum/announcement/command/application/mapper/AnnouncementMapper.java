package com.dao.momentum.announcement.command.application.mapper;

import com.dao.momentum.announcement.command.application.dto.request.AnnouncementCreateRequest;
import com.dao.momentum.announcement.command.application.dto.response.AnnouncementCreateResponse;
import com.dao.momentum.announcement.command.domain.aggregate.Announcement;
import org.springframework.stereotype.Component;

@Component
public class AnnouncementMapper {

    public Announcement toCreateEntity(AnnouncementCreateRequest dto, Long empId) {
        return Announcement.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .empId(empId)
                .build();
    }

    public AnnouncementCreateResponse toCreateResponse(Announcement announcement) {
        return AnnouncementCreateResponse.builder()
                .announcementId(announcement.getAnnouncementId())
                .build();
    }
}