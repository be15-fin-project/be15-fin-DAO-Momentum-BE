package com.dao.momentum.evaluation.eval.command.application.facade;

import ch.qos.logback.core.boolex.EvaluationException;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.common.kafka.dto.NotificationMessage;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalRoundCreateRequest;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalRoundUpdateRequest;
import com.dao.momentum.evaluation.eval.command.application.dto.response.EvalRoundCreateResponse;
import com.dao.momentum.evaluation.eval.command.application.dto.response.EvalRoundDeleteResponse;
import com.dao.momentum.evaluation.eval.command.application.dto.response.EvalRoundUpdateResponse;
import com.dao.momentum.evaluation.eval.command.application.service.EvalRoundService;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalRound;
import com.dao.momentum.evaluation.eval.command.domain.repository.EvalRoundRepository;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import com.dao.momentum.evaluation.hr.command.application.service.HrRateService;
import com.dao.momentum.evaluation.hr.command.application.service.HrWeightService;
import com.dao.momentum.notification.command.application.service.NotificationClient;
import com.dao.momentum.notification.command.application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvalRoundCommandFacade {

    private final EvalRoundService evalRoundService;
    private final HrWeightService hrWeightService;
    private final HrRateService hrRateService;
    private final NotificationClient notificationClient;
    private final NotificationService notificationService;
    private final EvalRoundRepository evalRoundRepository;

    @Transactional
    public EvalRoundCreateResponse createEvalRound(EvalRoundCreateRequest request) {
        log.info("[EvalRoundCommandFacade] createEvalRound() 호출 시작 - roundNo={}, startAt={}", request.getRoundNo(), request.getStartAt());

        try {
            var roundDto = request.toRoundDto();
            EvalRound round = evalRoundService.create(roundDto);

            log.info("평가 회차 엔티티 생성 완료 - roundId={}, roundNo={}", round.getRoundId(), round.getRoundNo());

            hrWeightService.create(round.getRoundId(), request.toWeightDto());
            hrRateService.create(round.getRoundId(), request.toRateDto());

            log.info("HR 가중치 및 비율 정보 저장 완료 - roundId={}", round.getRoundId());

            LocalDate startDate = round.getStartAt();
            LocalDate endDate = startDate.plusDays(6);

            if (startDate.isEqual(LocalDate.now())) {
                notificationClient.sendEvaluationStart(NotificationMessage.builder()
                        .type("EVALUATION_START")
                        .url("/performance/evaluation/my/list")
                        .build());
                log.info("즉시 알림 발송 완료 - roundId={}, startDate={}", round.getRoundId(), startDate);
            } else {
                notificationService.saveReservation(startDate, startDate, endDate, "EVALUATION_START");
                log.info("알림 예약 저장 완료 (시작 예정) - roundId={}, startDate={}", round.getRoundId(), startDate);
            }

            notificationService.saveReservation(endDate, startDate, endDate, "EVALUATION_END");
            log.info("알림 예약 저장 완료 (마감 예정) - roundId={}, endDate={}", round.getRoundId(), endDate);

            log.info("평가 회차 등록 완료 - roundId={}, roundNo={}", round.getRoundId(), round.getRoundNo());

            return EvalRoundCreateResponse.from(round);

        } catch (Exception e) {
            log.error("평가 회차 등록 실패 - roundNo={}, startAt={}, 에러={}", request.getRoundNo(), request.getStartAt(), e.getMessage());
            throw new EvalException(ErrorCode.EVAL_ROUND_CREATION_FAILED);
        }
    }

    @Transactional
    public EvalRoundUpdateResponse updateEvalRound(Integer roundId, EvalRoundUpdateRequest request) {
        log.info("[EvalRoundCommandFacade] updateEvalRound() 호출 시작 - roundId={}", roundId);

        try {
            var roundDto = request.toRoundDto(roundId);
            EvalRoundUpdateResponse round = evalRoundService.update(roundId, roundDto);

            log.info("평가 회차 수정 완료 - roundId={}", round.getRoundId());

            hrWeightService.update(round.getRoundId(), request.toWeightDto());
            hrRateService.update(round.getRoundId(), request.toRateDto());

            log.info("HR 가중치 및 비율 정보 수정 완료 - roundId={}", round.getRoundId());

            return EvalRoundUpdateResponse.builder()
                    .roundId(round.getRoundId())
                    .message("평가 회차가 성공적으로 수정되었습니다.")
                    .build();

        } catch (Exception e) {
            log.error("평가 회차 수정 실패 - roundId={}, 에러={}", roundId, e.getMessage());
            throw new EvalException(ErrorCode.EVAL_ROUND_UPDATE_FAILED);
        }
    }

    @Transactional
    public EvalRoundDeleteResponse deleteEvalRound(Integer roundId) {
        log.info("[EvalRoundCommandFacade] deleteEvalRound() 호출 시작 - roundId={}", roundId);

        try {
            var roundOptional = evalRoundRepository.findById(roundId); // 삭제 전 엔티티 조회

            if (roundOptional.isPresent()) {
                EvalRound round = roundOptional.get(); // 값이 존재하는 경우

                log.info("삭제될 평가 회차 정보 - roundId={}, roundNo={}", round.getRoundId(), round.getRoundNo());
            } else {
                throw new EvalException(ErrorCode.EVAL_ROUND_NOT_FOUND);
            }

            hrWeightService.deleteByRoundId(roundId);
            hrRateService.deleteByRoundId(roundId);
            evalRoundService.delete(roundId);

            log.info("HR 가중치 및 비율 정보 삭제 완료 - roundId={}", roundId);
            log.info("평가 회차 삭제 완료 - roundId={}", roundId);

            return EvalRoundDeleteResponse.builder()
                    .roundId(roundId)
                    .message("평가 회차가 성공적으로 삭제되었습니다.")
                    .build();

        } catch (Exception e) {
            log.error("평가 회차 삭제 실패 - roundId={}, 에러={}", roundId, e.getMessage());
            throw new EvalException(ErrorCode.EVAL_ROUND_DELETE_FAILED);
        }
    }
}
