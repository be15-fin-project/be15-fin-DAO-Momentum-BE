package com.dao.momentum.announcement.command.application.mapper;

import com.dao.momentum.announcement.command.application.dto.request.AnnouncementCreateRequest;
import com.dao.momentum.announcement.command.application.dto.response.AnnouncementCreateResponse;
import com.dao.momentum.announcement.command.application.dto.response.AnnouncementModifyResponse;
import com.dao.momentum.announcement.command.domain.aggregate.Announcement;
import com.dao.momentum.announcement.command.domain.aggregate.IsDeleted;
import org.springframework.stereotype.Component;

@Component
public class AnnouncementMapper {

    public Announcement toCreateEntity(AnnouncementCreateRequest dto, Long empId) {
        return Announcement.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .empId(empId)
                .isDeleted(IsDeleted.N)
                .build();
    }

    public AnnouncementCreateResponse toCreateResponse(Announcement announcement) {
        return AnnouncementCreateResponse.builder()
                .announcementId(announcement.getAnnouncementId())
                .build();
    }

    public AnnouncementModifyResponse toModifyResponse(Announcement announcement) {
        return AnnouncementModifyResponse.builder()
                .announcementId(announcement.getAnnouncementId())
                .build();
    }
}