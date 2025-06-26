package com.dao.momentum.retention.prospect.command.infrastructure.repository;

import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionRound;
import com.dao.momentum.retention.prospect.command.domain.repository.RetentionRoundRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface JpaRetentionRoundRepository extends JpaRepository<RetentionRound, Integer>, RetentionRoundRepository {
    int countByYearAndMonth(int year, int month);
}
