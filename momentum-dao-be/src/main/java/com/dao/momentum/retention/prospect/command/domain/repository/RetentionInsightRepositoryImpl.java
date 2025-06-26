package com.dao.momentum.retention.prospect.command.domain.repository;

import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionInsight;
import com.dao.momentum.retention.prospect.command.infrastructure.repository.JpaRetentionInsightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RetentionInsightRepositoryImpl implements RetentionInsightRepository {

    private final JpaRetentionInsightRepository jpaRepository;

    @Override
    public void saveAllInsights(List<RetentionInsight> insights) {
        jpaRepository.saveAll(insights);
    }
}
