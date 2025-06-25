package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalRoundCreateDTO;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalRoundUpdateDTO;
import com.dao.momentum.evaluation.eval.command.application.dto.response.EvalRoundUpdateResponse;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalRound;
import com.dao.momentum.evaluation.eval.command.domain.repository.EvalRoundRepository;
import com.dao.momentum.evaluation.eval.exception.EvalException;
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

    @Override
    @Transactional
    public EvalRoundUpdateResponse update(Integer roundId, EvalRoundUpdateDTO dto) {
        EvalRound round = evalRoundRepository.findById(roundId)
                .orElseThrow(() -> new EvalException(ErrorCode.EVAL_ROUND_NOT_FOUND));

        round.updateRound(dto.getRoundNo(), dto.getStartAt());

        return EvalRoundUpdateResponse.builder()
                .roundId(round.getRoundId())
                .message("평가 회차가 성공적으로 수정되었습니다.")
                .build();
    }
}
