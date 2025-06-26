package com.dao.momentum.retention.prospect.command.infrastructure.repository;

import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionSupport;
import com.dao.momentum.retention.prospect.command.domain.repository.RetentionSupportRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface JpaRetentionSupportRepository extends JpaRepository<RetentionSupport, Long> {
}
