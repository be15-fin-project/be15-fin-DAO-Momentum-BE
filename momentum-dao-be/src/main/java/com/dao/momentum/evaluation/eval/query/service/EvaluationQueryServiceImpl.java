package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import com.dao.momentum.evaluation.eval.query.dto.request.PeerEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.PeerEvaluationListResultDto;
import com.dao.momentum.evaluation.eval.query.dto.response.PeerEvaluationResponseDto;
import com.dao.momentum.evaluation.eval.query.mapper.PeerEvaluationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationQueryServiceImpl implements EvaluationQueryService {

    private final PeerEvaluationMapper peerEvaluationMapper;

    @Override
    public PeerEvaluationListResultDto getPeerEvaluations(PeerEvaluationListRequestDto requestDto) {
        long total = peerEvaluationMapper.countPeerEvaluations(requestDto);
        List<PeerEvaluationResponseDto> list = peerEvaluationMapper.findPeerEvaluations(requestDto);

        if (list == null || list.isEmpty()) {
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
}
