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
    public RetentionRound create(int year, int month, Integer roundNo) {
        // roundNo가 null이면 자동 채번
        int finalRoundNo = roundNo != null
                ? roundNo
                : retentionRoundRepository.findMaxRoundNo().orElse(0) + 1;

        RetentionRound round = RetentionRound.create(finalRoundNo, year, month);
        return retentionRoundRepository.save(round);
    }
}
