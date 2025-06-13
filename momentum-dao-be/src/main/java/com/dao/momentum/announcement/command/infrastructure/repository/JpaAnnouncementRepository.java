package com.dao.momentum.announcement.command.infrastructure.repository;


import com.dao.momentum.announcement.command.domain.aggregate.Announcement;
import com.dao.momentum.announcement.command.domain.repository.AnnouncementRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAnnouncementRepository extends AnnouncementRepository, JpaRepository<Announcement, Long> {

}
