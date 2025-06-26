package com.dao.momentum.retention.prospect.command.infrastructure.repository;

import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionInsight;
import com.dao.momentum.retention.prospect.command.domain.repository.RetentionInsightRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface JpaRetentionInsightRepository extends JpaRepository<RetentionInsight, Long> {
}
