package com.dao.momentum.retention.prospect.command.domain.repository;

import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionRound;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RetentionRoundRepository {
    RetentionRound save(RetentionRound round);
    int countByYearAndMonth(int year, int month);

    @Query("SELECT MAX(r.roundNo) FROM RetentionRound r")
    Optional<Integer> findMaxRoundNo();

}
