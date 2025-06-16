package com.dao.momentum.announcement.command.domain.repository;

import com.dao.momentum.announcement.command.domain.aggregate.Announcement;

import java.util.Optional;

public interface AnnouncementRepository {
    Announcement save(Announcement announcement);

    Optional<Announcement> findById(Long announcementId);

    void deleteById(Long announcementId);
}
