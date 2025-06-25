package com.dao.momentum.retention.command.domain.repository;

import com.dao.momentum.retention.command.domain.aggregate.RetentionContact;

import java.util.Optional;

public interface RetentionContactRepository {
    RetentionContact save(RetentionContact contact);

    Optional<RetentionContact> findById(Long aLong);
}
