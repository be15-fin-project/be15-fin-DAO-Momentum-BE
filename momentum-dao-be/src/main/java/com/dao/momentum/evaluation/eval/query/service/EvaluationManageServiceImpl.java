package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvaluationRoundStatus;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationFormListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationFormPropertyRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationRoundListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.*;
import com.dao.momentum.evaluation.eval.query.mapper.EvaluationManageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationManageServiceImpl implements EvaluationManageService {

    private final EvaluationManageMapper evaluationManageMapper;

    // 평가 회차 조회
    @Override
    @Transactional(readOnly = true)
    public EvaluationRoundListResultDto getEvaluationRounds(EvaluationRoundListRequestDto request) {

        long total = evaluationManageMapper.countEvaluationRounds(request);
        if (total == 0) {
            return new EvaluationRoundListResultDto(List.of(), buildPagination(request.getPage(), request.getSize(), 0));
        }

        List<EvaluationRoundResponseDto> rawList = evaluationManageMapper.findEvaluationRounds(request);
        LocalDate today = LocalDate.now();

        // 상태 계산 후 필터링
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
                .toList();

        return new EvaluationRoundListResultDto(filtered, buildPagination(request.getPage(), request.getSize(), total));
    }

    // 평가 종류 조회
    @Override
    @Transactional(readOnly = true)
    public List<EvaluationFormResponseDto> getEvaluationForms(EvaluationFormListRequestDto request) {
        return evaluationManageMapper.findEvaluationForms(request);
    }

    // 평가 종류 트리 조회
    @Override
    @Transactional(readOnly = true)
    public List<EvaluationTypeTreeResponseDto> getFormTree() {
        List<EvaluationTypeDto> types = evaluationManageMapper.findAllEvalTypes();
        List<EvaluationFormDto> forms = evaluationManageMapper.findAllActiveForms();

        if (types.isEmpty()) {
            throw new EvalException(ErrorCode.EVALUATION_TYPE_NOT_FOUND); // 예외 추가 필요
        }

        Map<Long, List<EvaluationFormDto>> formMap = forms.stream()
                .collect(Collectors.groupingBy(EvaluationFormDto::typeId));

        return types.stream()
                .map(type -> EvaluationTypeTreeResponseDto.builder()
                        .typeId(type.typeId())
                        .typeName(type.typeName())
                        .description(type.description())
                        .children(formMap.getOrDefault(type.typeId(), List.of()))
                        .build())
                .toList();
    }

    // 평가 양식별 요인 조회
    @Override
    @Transactional(readOnly = true)
    public List<EvaluationFormPropertyDto> getFormProperties(EvaluationFormPropertyRequestDto request) {
        return evaluationManageMapper.findFormProperties(request.getFormId());
    }

    // 평가 진행 여부 조회
    @Override
    @Transactional(readOnly = true)
    public EvaluationRoundStatusDto getTodayRoundStatus() {
        LocalDate today = LocalDate.now();
        List<Long> ongoing = evaluationManageMapper.findOngoingRoundIds(today);

        boolean inProgress = !ongoing.isEmpty();
        // 가장 최신 회차 하나만 선택
        Long roundId = inProgress ? ongoing.get(0) : null;

        return EvaluationRoundStatusDto.builder()
                .inProgress(inProgress)
                .roundId(roundId)
                .build();
    }

    // 평가 회차 번호 조회
    @Override
    public List<EvaluationRoundSimpleDto> getSimpleRoundList() {
        return evaluationManageMapper.findSimpleRounds();
    }

    // 페이지네이션 계산
    private Pagination buildPagination(int page, int size, long total) {
        int totalPage = (int) Math.ceil((double) total / size);
        return Pagination.builder()
                .currentPage(page)
                .totalPage(totalPage)
                .totalItems(total)
                .build();
    }
}
