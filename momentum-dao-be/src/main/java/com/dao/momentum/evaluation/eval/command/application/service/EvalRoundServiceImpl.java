package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalRoundCreateDTO;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalRoundUpdateDTO;
import com.dao.momentum.evaluation.eval.command.application.dto.response.EvalRoundUpdateResponse;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalRound;
import com.dao.momentum.evaluation.eval.command.domain.repository.EvalRoundRepository;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvalRoundServiceImpl implements EvalRoundService {

    private final EvalRoundRepository evalRoundRepository;

    @Transactional
    @Override
    public EvalRound create(EvalRoundCreateDTO dto) {
        log.info("[EvalRoundServiceImpl] create() 호출 시작 - roundNo={}, startAt={}", dto.getRoundNo(), dto.getStartAt());

        // 이미 존재하는 roundNo 체크
        if (evalRoundRepository.existsByRoundNo(dto.getRoundNo())) {
            log.error("이미 존재하는 평가 회차 - roundNo={}", dto.getRoundNo());
            throw new EvalException(ErrorCode.EVAL_ROUND_DUPLICATE);
        }

        // 시작일자가 현재 날짜 이전인지 확인
        if (dto.getStartAt().isBefore(java.time.LocalDate.now())) {
            log.error("유효하지 않은 시작일자 - roundNo={}, startAt={}", dto.getRoundNo(), dto.getStartAt());
            throw new EvalException(ErrorCode.EVAL_ROUND_INVALID_START_DATE);
        }

        EvalRound evalRound = EvalRound.builder()
                .roundNo(dto.getRoundNo())
                .startAt(dto.getStartAt())
                .build();

        EvalRound saved = evalRoundRepository.save(evalRound);
        log.info("평가 회차 저장 완료 - roundId={}, roundNo={}", saved.getRoundId(), saved.getRoundNo());

        return saved;
    }

    @Override
    @Transactional
    public EvalRoundUpdateResponse update(Integer roundId, EvalRoundUpdateDTO dto) {
        log.info("[EvalRoundServiceImpl] update() 호출 시작 - roundId={}, newStartAt={}", roundId, dto.getStartAt());

        // 시작일자가 현재 날짜보다 내일 이후인지 확인
        if (dto.getStartAt().isBefore(java.time.LocalDate.now().plusDays(1))) {
            log.error("유효하지 않은 시작일자 - roundId={}, newStartAt={}", roundId, dto.getStartAt());
            throw new EvalException(ErrorCode.EVAL_ROUND_INVALID_START_DATE);
        }

        // 평가 회차가 존재하는지 확인
        EvalRound round = evalRoundRepository.findById(roundId)
                .orElseThrow(() -> {
                    log.error("평가 회차를 찾을 수 없습니다. - roundId={}", roundId);
                    return new EvalException(ErrorCode.EVAL_ROUND_NOT_FOUND);
                });

        round.updateRound(dto.getRoundNo(), dto.getStartAt());
        log.info("평가 회차 수정 완료 - roundId={}, roundNo={}, startAt={}", round.getRoundId(), round.getRoundNo(), round.getStartAt());

        return EvalRoundUpdateResponse.builder()
                .roundId(round.getRoundId())
                .message("평가 회차가 성공적으로 수정되었습니다.")
                .build();
    }

    @Override
    @Transactional
    public void delete(Integer roundId) {
        log.info("[EvalRoundServiceImpl] delete() 호출 시작 - roundId={}", roundId);

        // 평가 회차 존재 여부 확인
        if (!evalRoundRepository.existsById(roundId)) {
            log.error("평가 회차를 찾을 수 없습니다 - roundId={}", roundId);
            throw new EvalException(ErrorCode.EVAL_ROUND_NOT_FOUND);
        }

        evalRoundRepository.deleteById(roundId);
        log.info("평가 회차 삭제 완료 - roundId={}", roundId);
    }
}
