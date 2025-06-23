package com.dao.momentum.file.command.infrastructure.repository;

import com.dao.momentum.file.command.domain.aggregate.File;
import com.dao.momentum.file.command.domain.repository.FileRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaFileRepository extends JpaRepository<File, Long>, FileRepository {
    List<File> findAllByAnnouncementId(Long announcementId);
}
