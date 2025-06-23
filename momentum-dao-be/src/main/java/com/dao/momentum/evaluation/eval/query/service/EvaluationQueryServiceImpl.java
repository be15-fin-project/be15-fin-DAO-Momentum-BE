package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import com.dao.momentum.evaluation.eval.query.dto.request.OrgEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.PeerEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.SelfEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.*;
import com.dao.momentum.evaluation.eval.query.mapper.OrgEvaluationMapper;
import com.dao.momentum.evaluation.eval.query.mapper.PeerEvaluationMapper;
import com.dao.momentum.evaluation.eval.query.mapper.SelfEvaluationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationQueryServiceImpl implements EvaluationQueryService {

    private final PeerEvaluationMapper peerEvaluationMapper;
    private final OrgEvaluationMapper orgEvaluationMapper;
    private final SelfEvaluationMapper selfEvaluationMapper;

    // 사원 간 평가 내역 조회
    @Override
    @Transactional(readOnly = true)
    public PeerEvaluationListResultDto getPeerEvaluations(PeerEvaluationListRequestDto requestDto) {
        long total = peerEvaluationMapper.countPeerEvaluations(requestDto);
        List<PeerEvaluationResponseDto> list = peerEvaluationMapper.findPeerEvaluations(requestDto);

        if (list == null) {
            throw new EvalException(ErrorCode.EVALUATION_RESULT_NOT_FOUND);
        }

        Pagination pagination = buildPagination(requestDto.getPage(), requestDto.getSize(), total);

        return new PeerEvaluationListResultDto(list, pagination);
    }

    // 사원 간 평가 상세 조회
    @Override
    @Transactional(readOnly = true)
    public PeerEvaluationDetailResultDto getPeerEvaluationDetail(Long resultId) {
        PeerEvaluationResponseDto detail = peerEvaluationMapper.findPeerEvaluationDetail(resultId);

        if (detail == null) {
            throw new EvalException(ErrorCode.EVALUATION_RESULT_NOT_FOUND);
        }

        List<FactorScoreDto> factorScores = peerEvaluationMapper.findFactorScores(resultId);

        return PeerEvaluationDetailResultDto.builder()
                .detail(detail)
                .factorScores(factorScores)
                .build();
    }

    // 조직 평가 내역 조회
    @Override
    @Transactional(readOnly = true)
    public OrgEvaluationListResultDto getOrgEvaluations(OrgEvaluationListRequestDto requestDto) {
        long total = orgEvaluationMapper.countOrgEvaluations(requestDto);
        List<OrgEvaluationResponseDto> list = orgEvaluationMapper.findOrgEvaluations(requestDto);

        if (list == null) {
            throw new EvalException(ErrorCode.EVALUATION_RESULT_NOT_FOUND);
        }

        Pagination pagination = buildPagination(requestDto.getPage(), requestDto.getSize(), total);

        return new OrgEvaluationListResultDto(list, pagination);
    }

    // 조직 평가 상세 조회
    @Override
    @Transactional(readOnly = true)
    public OrgEvaluationDetailResultDto getOrgEvaluationDetail(Long resultId) {
        OrgEvaluationResponseDto detail = orgEvaluationMapper.findOrgEvaluationDetail(resultId);
        if (detail == null) {
            throw new EvalException(ErrorCode.EVALUATION_RESULT_NOT_FOUND);
        }

        List<FactorScoreDto> factorScores = orgEvaluationMapper.findOrgFactorScores(resultId);

        return OrgEvaluationDetailResultDto.builder()
                .detail(detail)
                .factorScores(factorScores)
                .build();
    }

    // 자가 진단 내역 조회
    @Override
    @Transactional(readOnly = true)
    public SelfEvaluationListResultDto getSelfEvaluations(SelfEvaluationListRequestDto requestDto) {
        long total = selfEvaluationMapper.countSelfEvaluations(requestDto);
        List<SelfEvaluationResponseDto> list = selfEvaluationMapper.findSelfEvaluations(requestDto);

        if (list == null) {
            throw new EvalException(ErrorCode.EVALUATION_RESULT_NOT_FOUND);
        }

        Pagination pagination = buildPagination(requestDto.getPage(), requestDto.getSize(), total);

        return new SelfEvaluationListResultDto(list, pagination);
    }

    // 자가 진단 상세 조회
    @Override
    @Transactional(readOnly = true)
    public SelfEvaluationDetailResultDto getSelfEvaluationDetail(Long resultId) {
        SelfEvaluationResponseDto detail = selfEvaluationMapper.findSelfEvaluationDetail(resultId);

        if (detail == null) {
            throw new EvalException(ErrorCode.EVALUATION_RESULT_NOT_FOUND);
        }

        List<FactorScoreDto> factorScores = selfEvaluationMapper.findFactorScores(resultId);

        return SelfEvaluationDetailResultDto.builder()
                .detail(detail)
                .factorScores(factorScores)
                .build();
    }

    // 페이지네이션
    private Pagination buildPagination(int page, int size, long total) {
        int totalPage = (int) Math.ceil((double) total / size);
        return Pagination.builder()
                .currentPage(page)
                .totalPage(totalPage)
                .totalItems(total)
                .build();
    }
}
