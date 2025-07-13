package com.dao.momentum.file.command.domain.repository;

import com.dao.momentum.file.command.domain.aggregate.File;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FileRepository {
    File save(File file);

    Optional<File> findById(Long fileId);

    void deleteById(Long fileId);

    List<File> findAllByAnnouncementId(Long announcementId);

    Optional<File> findByContractId(long contractId);

    @Modifying
    @Query("DELETE FROM File f WHERE f.approveId = :approveId")
    void deleteByApprovalId(Long approveId);
}
