package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvaluationRoundStatus;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import com.dao.momentum.evaluation.eval.query.dto.request.*;
import com.dao.momentum.evaluation.eval.query.dto.response.*;
import com.dao.momentum.evaluation.eval.query.mapper.EvaluationManageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationManageServiceImpl implements EvaluationManageService {

    private final EvaluationManageMapper evaluationManageMapper;

    @Override
    @Transactional(readOnly = true)
    public EvaluationRoundListResultDto getEvaluationRounds(EvaluationRoundListRequestDto request) {
        log.info("[EvaluationManageServiceImpl] getEvaluationRounds() 호출 시작 - request={}", request);

        long total = evaluationManageMapper.countEvaluationRounds(request);
        if (total == 0) {
            log.info("조회 결과 없음 - total=0");
            return new EvaluationRoundListResultDto(List.of(), buildPagination(request.getPage(), request.getSize(), 0));
        }

        List<EvaluationRoundResponseDto> rawList = evaluationManageMapper.findEvaluationRounds(request);
        LocalDate today = LocalDate.now();

        List<EvaluationRoundResponseDto> filtered = rawList.stream()
                .map(dto -> {
                    EvaluationRoundStatus status = EvaluationRoundStatus.from(dto.getStartAt(), dto.getEndAt(), today);
                    return EvaluationRoundResponseDto.builder()
                            .roundId(dto.getRoundId())
                            .roundNo(dto.getRoundNo())
                            .startAt(dto.getStartAt())
                            .endAt(dto.getEndAt())
                            .participantCount(dto.getParticipantCount())
                            .status(status)
                            .build();
                })
                .filter(dto -> request.getStatus() == null || dto.getStatus() == request.getStatus())
                .collect(Collectors.toList());

        log.info("평가 회차 목록 조회 완료 - total={}, filtered={}", total, filtered.size());
        return new EvaluationRoundListResultDto(filtered, buildPagination(request.getPage(), request.getSize(), total));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvaluationFormResponseDto> getEvaluationForms(EvaluationFormListRequestDto request) {
        log.info("[EvaluationManageServiceImpl] getEvaluationForms() 호출 시작 - request={}", request);

        List<EvaluationFormResponseDto> result = evaluationManageMapper.findEvaluationForms(request);
        log.info("평가 종류 조회 완료 - count={}", result.size());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvaluationTypeTreeResponseDto> getFormTree() {
        log.info("[EvaluationManageServiceImpl] getFormTree() 호출 시작");

        List<EvaluationTypeDto> types = evaluationManageMapper.findAllEvalTypes();
        List<EvaluationFormDto> forms = evaluationManageMapper.findAllActiveForms();

        if (types.isEmpty()) {
            log.error("평가 종류 정보 없음");
            throw new EvalException(ErrorCode.EVALUATION_TYPE_NOT_FOUND);
        }

        Map<Long, List<EvaluationFormDto>> formMap = forms.stream()
                .collect(Collectors.groupingBy(EvaluationFormDto::typeId));

        List<EvaluationTypeTreeResponseDto> result = types.stream()
                .map(type -> EvaluationTypeTreeResponseDto.builder()
                        .typeId(type.typeId())
                        .typeName(type.typeName())
                        .description(type.description())
                        .children(formMap.getOrDefault(type.typeId(), List.of()))
                        .build())
                .collect(Collectors.toList());

        log.info("평가 종류 트리 조회 완료 - typeCount={}, formCount={}", types.size(), forms.size());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvaluationFormPropertyDto> getFormProperties(EvaluationFormPropertyRequestDto request) {
        log.info("[EvaluationManageServiceImpl] getFormProperties() 호출 시작 - formId={}", request.getFormId());

        List<EvaluationFormPropertyDto> result = evaluationManageMapper.findFormProperties(request.getFormId());
        log.info("평가 양식별 요인 조회 완료 - count={}", result.size());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluationRoundStatusDto getTodayRoundStatus() {
        log.info("[EvaluationManageServiceImpl] getTodayRoundStatus() 호출 시작");

        LocalDate today = LocalDate.now();
        List<Long> ongoing = evaluationManageMapper.findOngoingRoundIds(today);

        boolean inProgress = !ongoing.isEmpty();
        Long roundId = inProgress ? ongoing.get(0) : null;

        log.info("오늘의 평가 진행 상태 조회 완료 - inProgress={}, roundId={}", inProgress, roundId);
        return EvaluationRoundStatusDto.builder()
                .inProgress(inProgress)
                .roundId(roundId)
                .build();
    }

    @Override
    public List<EvaluationRoundSimpleDto> getSimpleRoundList() {
        log.info("[EvaluationManageServiceImpl] getSimpleRoundList() 호출 시작");

        List<EvaluationRoundSimpleDto> result = evaluationManageMapper.findSimpleRounds();
        log.info("평가 회차 번호 리스트 조회 완료 - count={}", result.size());
        return result;
    }

    private Pagination buildPagination(int page, int size, long total) {
        int totalPage = (int) Math.ceil((double) total / size);
        return Pagination.builder()
                .currentPage(page)
                .totalPage(totalPage)
                .totalItems(total)
                .build();
    }
}
