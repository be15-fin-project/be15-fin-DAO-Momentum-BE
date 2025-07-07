package com.dao.momentum.retention.prospect.command.domain.repository;

import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionRound;

public interface RetentionRoundCommandService {
    RetentionRound create(int year, int month, Integer roundNo);
}
