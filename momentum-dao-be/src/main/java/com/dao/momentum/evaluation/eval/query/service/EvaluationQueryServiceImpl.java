package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import com.dao.momentum.evaluation.eval.query.dto.request.PeerEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.*;
import com.dao.momentum.evaluation.eval.query.mapper.PeerEvaluationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationQueryServiceImpl implements EvaluationQueryService {

    private final PeerEvaluationMapper peerEvaluationMapper;

    // 사원 간 평가 내역 조회
    @Override
    public PeerEvaluationListResultDto getPeerEvaluations(PeerEvaluationListRequestDto requestDto) {
        long total = peerEvaluationMapper.countPeerEvaluations(requestDto);
        List<PeerEvaluationResponseDto> list = peerEvaluationMapper.findPeerEvaluations(requestDto);

        if (list == null) {
            throw new EvalException(ErrorCode.EVALUATION_RESULT_NOT_FOUND);
        }

        int totalPage = (int) Math.ceil((double) total / requestDto.getSize());
        Pagination pagination = Pagination.builder()
                .currentPage(requestDto.getPage())
                .totalPage(totalPage)
                .totalItems(total)
                .build();

        return new PeerEvaluationListResultDto(list, pagination);
    }

    // 사원 간 평가 상세 조회
    @Override
    public PeerEvaluationDetailResultDto getPeerEvaluationDetail(Long resultId) {
        PeerEvaluationDetailResponseDto detail = peerEvaluationMapper.findPeerEvaluationDetail(resultId);

        if (detail == null) {
            throw new EvalException(ErrorCode.EVALUATION_RESULT_NOT_FOUND);
        }

        List<FactorScoreDto> factorScores = peerEvaluationMapper.findFactorScores(resultId);

        return PeerEvaluationDetailResultDto.builder()
                .detail(detail)
                .factorScores(factorScores)
                .build();
    }

}
