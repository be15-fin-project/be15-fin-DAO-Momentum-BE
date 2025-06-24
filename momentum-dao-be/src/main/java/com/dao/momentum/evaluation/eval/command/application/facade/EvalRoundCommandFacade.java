package com.dao.momentum.evaluation.eval.command.application.facade;

import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalRoundCreateRequest;
import com.dao.momentum.evaluation.eval.command.application.dto.response.EvalRoundCreateResponse;
import com.dao.momentum.evaluation.eval.command.application.service.EvalRoundService;
import com.dao.momentum.evaluation.hr.command.application.service.HrRateService;
import com.dao.momentum.evaluation.hr.command.application.service.HrWeightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EvalRoundCommandFacade {

    private final EvalRoundService evalRoundService;
    private final HrWeightService hrWeightService;
    private final HrRateService hrRateService;

    @Transactional
    public EvalRoundCreateResponse createEvalRound(EvalRoundCreateRequest request) {
        var roundDto = request.toRoundDto();
        var round = evalRoundService.create(roundDto);

        hrWeightService.create(round.getRoundId(), request.toWeightDto());
        hrRateService.create(round.getRoundId(), request.toRateDto());

        return EvalRoundCreateResponse.from(round);
    }
}
