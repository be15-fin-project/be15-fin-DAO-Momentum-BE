package com.dao.momentum.evaluation.eval.command.application.facade;

import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalRoundCreateRequest;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalRoundUpdateRequest;
import com.dao.momentum.evaluation.eval.command.application.dto.response.EvalRoundCreateResponse;
import com.dao.momentum.evaluation.eval.command.application.dto.response.EvalRoundDeleteResponse;
import com.dao.momentum.evaluation.eval.command.application.dto.response.EvalRoundUpdateResponse;
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

    @Transactional
    public EvalRoundUpdateResponse updateEvalRound(Integer roundId, EvalRoundUpdateRequest request) {
        var roundDto = request.toRoundDto(roundId);
        var round = evalRoundService.update(roundId, roundDto);

        hrWeightService.update(round.getRoundId(), request.toWeightDto());
        hrRateService.update(round.getRoundId(), request.toRateDto());

        return EvalRoundUpdateResponse.builder()
                .roundId(round.getRoundId())
                .message("평가 회차가 성공적으로 수정되었습니다.")
                .build();
    }

    @Transactional
    public EvalRoundDeleteResponse deleteEvalRound(Integer roundId) {
        // 1. 가중치 삭제
        hrWeightService.deleteByRoundId(roundId);

        // 2. 등급 삭제
        hrRateService.deleteByRoundId(roundId);

        // 3. 회차 삭제
        evalRoundService.delete(roundId);

        // 4. 응답 생성
        return EvalRoundDeleteResponse.builder()
                .roundId(roundId)
                .message("평가 회차가 성공적으로 삭제되었습니다.")
                .build();
    }

}
