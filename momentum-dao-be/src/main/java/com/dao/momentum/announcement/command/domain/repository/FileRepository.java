package com.dao.momentum.announcement.command.domain.repository;

import com.dao.momentum.announcement.command.domain.aggregate.File;

import java.util.List;
import java.util.Optional;

public interface FileRepository {
    File save(File file);

    Optional<File> findById(Long fileId);

    void deleteById(Long fileId);

    List<File> findAllByAnnouncementId(Long announcementId);
}
