package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvaluationRoundStatus;
import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationFormListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationRoundListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationFormResponseDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationRoundListResultDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationRoundResponseDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationRoundSimpleDto;
import com.dao.momentum.evaluation.eval.query.mapper.EvaluationManageMapper;
import com.dao.momentum.evaluation.eval.query.service.EvaluationManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationManageServiceImpl implements EvaluationManageService {

    private final EvaluationManageMapper evaluationManageMapper;

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
