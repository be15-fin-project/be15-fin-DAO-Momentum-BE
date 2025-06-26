package com.dao.momentum.retention.prospect.command.domain.repository;

import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionRound;

public interface RetentionRoundRepository {
    RetentionRound save(RetentionRound round);
    int countByYearAndMonth(int year, int month);
}
