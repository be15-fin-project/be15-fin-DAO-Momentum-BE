package com.dao.momentum.retention.command.infrastructure.repository;

import com.dao.momentum.retention.command.domain.aggregate.RetentionContact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRetentionContactRepository extends JpaRepository<RetentionContact, Long> {
}
