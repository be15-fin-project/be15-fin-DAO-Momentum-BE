package com.dao.momentum.retention.command.domain.repository;

import com.dao.momentum.retention.command.domain.aggregate.RetentionContact;

public interface RetentionContactRepository {
    RetentionContact save(RetentionContact contact);
}
