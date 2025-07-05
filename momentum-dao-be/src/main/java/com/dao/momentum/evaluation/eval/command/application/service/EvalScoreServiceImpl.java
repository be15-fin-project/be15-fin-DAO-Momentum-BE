package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalFactorScoreDto;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalScore;
import com.dao.momentum.evaluation.eval.command.domain.repository.EvalScoreRepository;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvalScoreServiceImpl implements EvalScoreService {

    private final EvalScoreRepository evalScoreRepository;

    @Override
    @Transactional
    public void save(EvalScore score) {
        log.info("[EvalScoreServiceImpl] save() 호출 시작 - resultId={}, propertyId={}", score.getResultId(), score.getPropertyId());

        try {
            evalScoreRepository.save(score);
            log.info("평가 점수 저장 완료 - resultId={}, propertyId={}", score.getResultId(), score.getPropertyId());
        } catch (Exception e) {
            log.error("평가 점수 저장 실패 - resultId={}, propertyId={}, 에러={}", score.getResultId(), score.getPropertyId(), e.getMessage());
            throw new EvalException(ErrorCode.EVAL_SAVE_FAILED);
        }
    }

    @Override
    public void saveFactorScores(Long resultId, List<EvalFactorScoreDto> factorScores) {
        log.info("[EvalScoreServiceImpl] saveFactorScores() 호출 시작 - resultId={}, factorScoresCount={}", resultId, factorScores != null ? factorScores.size() : 0);

        if (resultId == null) {
            log.error("잘못된 resultId - resultId={}", resultId);
            throw new EvalException(ErrorCode.INVALID_RESULT_REQUEST);
        }

        if (factorScores == null || factorScores.isEmpty()) {
            log.error("빈 평가 항목 리스트 - resultId={}", resultId);
            throw new EvalException(ErrorCode.EVAL_INVALID_NOT_EXIST);
        }

        try {
            List<EvalScore> scores = factorScores.stream()
                    .map(dto -> EvalScore.builder()
                            .resultId(resultId)
                            .propertyId(dto.getPropertyId())
                            .score(dto.getScore())
                            .build())
                    .toList();

            evalScoreRepository.saveAll(scores);
            log.info("요인별 평가 점수 저장 완료 - resultId={}, savedCount={}", resultId, scores.size());
        } catch (Exception e) {
            log.error("요인별 평가 점수 저장 실패 - resultId={}, 에러={}", resultId, e.getMessage());
            throw new EvalException(ErrorCode.EVAL_SAVE_FAILED);
        }
    }

    @Override
    @Transactional
    public void updateScores(Long resultId, Map<Integer, Integer> scoreMap) {
        log.info("[EvalScoreServiceImpl] updateScores() 호출 시작 - resultId={}, updateCount={}", resultId, scoreMap.size());

        try {
            evalScoreRepository.deleteByResultId(resultId);

            for (Map.Entry<Integer, Integer> entry : scoreMap.entrySet()) {
                EvalScore score = EvalScore.builder()
                        .resultId(resultId)
                        .propertyId(entry.getKey())
                        .score(entry.getValue())
                        .build();
                evalScoreRepository.save(score);
            }

            log.info("평가 점수 갱신 완료 - resultId={}, updatedCount={}", resultId, scoreMap.size());
        } catch (Exception e) {
            log.error("평가 점수 갱신 실패 - resultId={}, 에러={}", resultId, e.getMessage());
            throw new EvalException(ErrorCode.EVAL_UPDATE_FAILED);
        }
    }

    @Override
    @Transactional
    public void deleteByResultId(Long resultId) {
        log.info("[EvalScoreServiceImpl] deleteByResultId() 호출 시작 - resultId={}", resultId);

        try {
            evalScoreRepository.deleteByResultId(resultId);
            log.info("평가 점수 삭제 완료 - resultId={}", resultId);
        } catch (Exception e) {
            log.error("평가 점수 삭제 실패 - resultId={}, 에러={}", resultId, e.getMessage());
            throw new EvalException(ErrorCode.EVAL_DELETE_FAILED);
        }
    }

    @Override
    @Transactional
    public void saveAll(List<EvalScore> scoreEntities) {
        log.info("[EvalScoreServiceImpl] saveAll() 호출 시작 - scoreCount={}", scoreEntities != null ? scoreEntities.size() : 0);

        try {
            evalScoreRepository.saveAll(scoreEntities);
            log.info("일괄 평가 점수 저장 완료 - savedCount={}", scoreEntities.size());
        } catch (Exception e) {
            log.error("일괄 평가 점수 저장 실패 - scoreCount={}, 에러={}", scoreEntities.size(), e.getMessage());
            throw new EvalException(ErrorCode.EVAL_SAVE_ALL_FAILED);
        }
    }
}
