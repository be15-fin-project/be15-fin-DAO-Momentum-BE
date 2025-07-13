package com.dao.momentum.retention.prospect.command.domain.repository;

import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionSupport;

import java.util.List;
import java.util.Optional;

public interface RetentionSupportRepository {
    void saveAllSupports(List<RetentionSupport> supports);
    Optional<RetentionSupport> findById(Long retentionId);
}
