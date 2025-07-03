package com.dao.momentum.evaluation.eval.command.application.facade;

import com.dao.momentum.common.kafka.dto.NotificationMessage;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalRoundCreateRequest;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalRoundUpdateRequest;
import com.dao.momentum.evaluation.eval.command.application.dto.response.EvalRoundCreateResponse;
import com.dao.momentum.evaluation.eval.command.application.dto.response.EvalRoundDeleteResponse;
import com.dao.momentum.evaluation.eval.command.application.dto.response.EvalRoundUpdateResponse;
import com.dao.momentum.evaluation.eval.command.application.service.EvalRoundService;
import com.dao.momentum.evaluation.hr.command.application.service.HrRateService;
import com.dao.momentum.evaluation.hr.command.application.service.HrWeightService;
import com.dao.momentum.notification.command.application.service.NotificationClient;
import com.dao.momentum.notification.command.application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EvalRoundCommandFacade {

    private final EvalRoundService evalRoundService;
    private final HrWeightService hrWeightService;
    private final HrRateService hrRateService;
    private final NotificationClient notificationClient;
    private final NotificationService notificationService;

    /*
      평가 회차 등록 시
      - 평가 시작 날짜와 동일한 해당 날짜에 등록하면 바로 알림 발송
      - 평가 시작 날짜 이전에 등록하면 알림 발송 예약 테이블에 저장
     */
    @Transactional
    public EvalRoundCreateResponse createEvalRound(EvalRoundCreateRequest request) {
        var roundDto = request.toRoundDto();
        var round = evalRoundService.create(roundDto);

        hrWeightService.create(round.getRoundId(), request.toWeightDto());
        hrRateService.create(round.getRoundId(), request.toRateDto());

        LocalDate startDate = round.getStartAt();
        LocalDate endDate = startDate.plusDays(6);

        if (startDate.isEqual(LocalDate.now())) {
            // 당일 시작인 경우, 즉시 알림 발송
            NotificationMessage message = NotificationMessage.builder()
                    .type("EVALUATION_START")
                    .url("/performance/evaluation/my/list")
                    .build();

            // 알림 배치 서버 api 호출
            notificationClient.sendEvaluationStart(message);
        } else {
            // 시작 예정일 예약
            notificationService.saveReservation(startDate, startDate, endDate, "EVALUATION_START");
        }

        // 마감 예정일 예약은 항상 저장
        notificationService.saveReservation(endDate, startDate, endDate, "EVALUATION_END");

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
