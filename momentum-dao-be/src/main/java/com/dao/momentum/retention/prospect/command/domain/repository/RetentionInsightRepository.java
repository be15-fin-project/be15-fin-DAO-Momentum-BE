package com.dao.momentum.retention.prospect.command.domain.repository;

import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionInsight;

import java.util.List;

public interface RetentionInsightRepository {
    void saveAllInsights(List<RetentionInsight> insights);
}
