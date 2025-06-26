package com.dao.momentum.retention.prospect.command.application.service;

import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionSupport;
import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionInsight;

import java.util.List;

public interface RetentionInsightCommandService {
    List<RetentionInsight> generateInsights(Integer roundId, List<RetentionSupport> supports);
    void saveAll(List<RetentionInsight> insights);
}
