package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import com.dao.momentum.evaluation.eval.query.dto.request.FactorScoreDto;
import com.dao.momentum.evaluation.eval.query.dto.request.various.OrgEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.various.PeerEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.various.SelfEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.various.*;
import com.dao.momentum.evaluation.eval.query.mapper.OrgEvaluationMapper;
import com.dao.momentum.evaluation.eval.query.mapper.PeerEvaluationMapper;
import com.dao.momentum.evaluation.eval.query.mapper.SelfEvaluationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
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
        log.info("[EvaluationQueryServiceImpl] getPeerEvaluations() 호출 시작 - request={}", requestDto);

        long total = peerEvaluationMapper.countPeerEvaluations(requestDto);

        List<PeerEvaluationResponseDto> list = peerEvaluationMapper.findPeerEvaluations(requestDto);

        if (list == null) {
            log.error("사원 간 평가 내역을 조회할 수 없음 - request={}", requestDto);
            throw new EvalException(ErrorCode.EVALUATION_RESULT_NOT_FOUND);
        }

        log.info("사원 간 평가 조회 완료 - total={}, listSize={}", total, list.size());
        Pagination pagination = buildPagination(requestDto.page(), requestDto.size(), total);
        return new PeerEvaluationListResultDto(list, pagination);
    }

    // 사원 간 평가 상세 조회
    @Override
    @Transactional(readOnly = true)
    public PeerEvaluationDetailResultDto getPeerEvaluationDetail(Long resultId) {
        log.info("[EvaluationQueryServiceImpl] getPeerEvaluationDetail() 호출 시작 - resultId={}", resultId);

        PeerEvaluationResponseDto detail = peerEvaluationMapper.findPeerEvaluationDetail(resultId);
        if (detail == null) {
            log.error("사원 간 평가 상세 내역을 찾을 수 없음 - resultId={}", resultId);
            throw new EvalException(ErrorCode.EVALUATION_RESULT_NOT_FOUND);
        }

        List<FactorScoreDto> factorScores = peerEvaluationMapper.findFactorScores(resultId);
        log.info("사원 간 평가 상세 조회 완료 - factorCount={}", factorScores.size());

        return PeerEvaluationDetailResultDto.builder()
                .detail(detail)
                .factorScores(factorScores)
                .build();
    }

    // 조직 평가 내역 조회
    @Override
    @Transactional(readOnly = true)
    public OrgEvaluationListResultDto getOrgEvaluations(OrgEvaluationListRequestDto requestDto) {
        log.info("[EvaluationQueryServiceImpl] getOrgEvaluations() 호출 시작 - request={}", requestDto);

        long total = orgEvaluationMapper.countOrgEvaluations(requestDto);
        if (total == 0) {
            log.info("조직 평가 결과 없음 - total=0");
            return new OrgEvaluationListResultDto(List.of(), buildPagination(requestDto.page(), requestDto.size(), 0));
        }

        List<OrgEvaluationResponseDto> list = orgEvaluationMapper.findOrgEvaluations(requestDto);

        if (list == null) {
            log.error("조직 평가 내역을 조회할 수 없음 - request={}", requestDto);
            throw new EvalException(ErrorCode.EVALUATION_RESULT_NOT_FOUND);
        }

        log.info("조직 평가 조회 완료 - total={}, listSize={}", total, list.size());
        Pagination pagination = buildPagination(requestDto.page(), requestDto.size(), total);
        return new OrgEvaluationListResultDto(list, pagination);
    }

    // 조직 평가 상세 조회
    @Override
    @Transactional(readOnly = true)
    public OrgEvaluationDetailResultDto getOrgEvaluationDetail(Long resultId) {
        log.info("[EvaluationQueryServiceImpl] getOrgEvaluationDetail() 호출 시작 - resultId={}", resultId);

        OrgEvaluationResponseDto detail = orgEvaluationMapper.findOrgEvaluationDetail(resultId);
        if (detail == null) {
            log.error("조직 평가 상세 내역을 찾을 수 없음 - resultId={}", resultId);
            throw new EvalException(ErrorCode.EVALUATION_RESULT_NOT_FOUND);
        }

        List<FactorScoreDto> factorScores = orgEvaluationMapper.findOrgFactorScores(resultId);
        log.info("조직 평가 상세 조회 완료 - factorCount={}", factorScores.size());

        return OrgEvaluationDetailResultDto.builder()
                .detail(detail)
                .factorScores(factorScores)
                .build();
    }

    // 자가 진단 내역 조회
    @Override
    @Transactional(readOnly = true)
    public SelfEvaluationListResultDto getSelfEvaluations(SelfEvaluationListRequestDto requestDto) {
        log.info("[EvaluationQueryServiceImpl] getSelfEvaluations() 호출 시작 - request={}", requestDto);

        long total = selfEvaluationMapper.countSelfEvaluations(requestDto);
        if (total == 0) {
            log.info("자가 진단 평가 결과 없음 - total=0");
            return new SelfEvaluationListResultDto(List.of(), buildPagination(requestDto.page(), requestDto.size(), 0));
        }

        List<SelfEvaluationResponseDto> list = selfEvaluationMapper.findSelfEvaluations(requestDto);

        if (list == null) {
            log.error("자가 진단 내역을 조회할 수 없음 - request={}", requestDto);
            throw new EvalException(ErrorCode.EVALUATION_RESULT_NOT_FOUND);
        }

        log.info("자가 진단 평가 조회 완료 - total={}, listSize={}", total, list.size());
        Pagination pagination = buildPagination(requestDto.page(), requestDto.size(), total);
        return new SelfEvaluationListResultDto(list, pagination);
    }

    // 자가 진단 상세 조회
    @Override
    @Transactional(readOnly = true)
    public SelfEvaluationDetailResultDto getSelfEvaluationDetail(Long resultId) {
        log.info("[EvaluationQueryServiceImpl] getSelfEvaluationDetail() 호출 시작 - resultId={}", resultId);

        SelfEvaluationResponseDto detail = selfEvaluationMapper.findSelfEvaluationDetail(resultId);
        if (detail == null) {
            log.error("자가 진단 상세 내역을 찾을 수 없음 - resultId={}", resultId);
            throw new EvalException(ErrorCode.EVALUATION_RESULT_NOT_FOUND);
        }

        List<FactorScoreDto> factorScores = selfEvaluationMapper.findFactorScores(resultId);
        log.info("자가 진단 상세 조회 완료 - factorCount={}", factorScores.size());

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
