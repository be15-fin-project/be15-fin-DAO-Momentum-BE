package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalRoundCreateDTO;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalRound;
import com.dao.momentum.evaluation.eval.command.domain.repository.EvalRoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EvalRoundServiceImpl implements EvalRoundService {

    private final EvalRoundRepository evalRoundRepository;

    @Transactional
    @Override
    public EvalRound create(EvalRoundCreateDTO dto) {
        EvalRound evalRound = EvalRound.builder()
                .roundNo(dto.getRoundNo())
                .startAt(dto.getStartAt())
                .build();

        return evalRoundRepository.save(evalRound);
    }
}
