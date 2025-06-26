package com.dao.momentum.retention.prospect.command.domain.repository;

import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionRound;
import com.dao.momentum.retention.prospect.command.domain.repository.RetentionRoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RetentionRoundCommandServiceImpl implements RetentionRoundCommandService {

    private final RetentionRoundRepository retentionRoundRepository;

    @Override
    public RetentionRound create(int year, int month) {
        int count = retentionRoundRepository.countByYearAndMonth(year, month);
        int roundNo = count + 1;

        RetentionRound round = RetentionRound.create(roundNo, year, month);
        return retentionRoundRepository.save(round);
    }
}
