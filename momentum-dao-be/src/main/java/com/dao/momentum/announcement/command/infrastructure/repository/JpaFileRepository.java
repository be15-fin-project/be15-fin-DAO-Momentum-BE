package com.dao.momentum.announcement.command.infrastructure.repository;

import com.dao.momentum.announcement.command.domain.aggregate.File;
import com.dao.momentum.announcement.command.domain.repository.FileRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JpaFileRepository extends JpaRepository<File, Long>, FileRepository {
    List<File> findAllByAnnouncementId(Long announcementId);
}
