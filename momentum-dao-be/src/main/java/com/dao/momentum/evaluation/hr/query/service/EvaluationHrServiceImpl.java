package com.dao.momentum.evaluation.hr.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.exception.HrException;
import com.dao.momentum.evaluation.hr.query.dto.request.MyHrEvaluationListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.*;
import com.dao.momentum.evaluation.hr.query.mapper.EvaluationHrMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationHrServiceImpl implements EvaluationHrService {

    private final EvaluationHrMapper mapper;

    // 사원의 HR 평가 내역을 조회하는 메서드
    @Override
    @Transactional(readOnly = true)
    public HrEvaluationListResultDto getHrEvaluations(long empId, MyHrEvaluationListRequestDto req) {
        log.info("[EvaluationHrServiceImpl] getHrEvaluations() 호출 시작 - empId={}, 요청 파라미터={}", empId, req);

        // 1) 평가 내역 조회
        List<HrEvaluationItemDto> items = mapper.findHrEvaluations(empId, req);
        if (items == null) {
            log.error("HR 평가 내역을 찾을 수 없음 - empId={}, req={}", empId, req);
            throw new HrException(ErrorCode.HR_EVALUATIONS_NOT_FOUND);
        }
        log.info("조회된 평가 내역 수: {}", items.size());

        // 2) 전체 건수 조회
        long total = mapper.countHrEvaluations(empId, req);
        log.info("총 평가 내역 수: {}", total);

        // 3) 각 평가별 요인 점수 조회 및 Wrapping DTO 생성
        List<HrEvaluationWithFactorsDto> wrappedItems = new ArrayList<>();
        for (HrEvaluationItemDto item : items) {
            List<FactorScoreDto> factorScores = mapper.findFactorScores(item.resultId());
            if (factorScores == null) {
                log.error("요인 점수를 찾을 수 없음 - resultId={}", item.resultId());
                throw new HrException(ErrorCode.FACTOR_SCORES_NOT_FOUND);
            }

            wrappedItems.add(HrEvaluationWithFactorsDto.builder()
                    .item(item)
                    .factorScores(factorScores)
                    .build());
        }
        log.info("요인 점수 포함 평가 항목 조립 완료 - wrappedItems.size={}", wrappedItems.size());

        // 4) 페이지네이션 정보 생성
        Pagination pagination = buildPagination(req.page(), total);
        log.info("페이지네이션 완료 - currentPage={}, totalPage={}, totalItems={}",
                pagination.getCurrentPage(), pagination.getTotalPage(), pagination.getTotalItems());

        // 5) 최종 결과 DTO 반환
        HrEvaluationListResultDto result = HrEvaluationListResultDto.builder()
                .items(wrappedItems)
                .pagination(pagination)
                .build();

        log.info("HR 평가 내역 반환 완료 - items.size={}", wrappedItems.size());
        return result;
    }

    // HR 평가 상세 내역을 조회하는 메서드
    @Override
    @Transactional(readOnly = true)
    public HrEvaluationDetailResultDto getHrEvaluationDetail(Long empId, Long resultId) {
        log.info("[EvaluationHrServiceImpl] getHrEvaluationDetail() 호출 시작 - empId={}, resultId={}, 요청 파라미터={}, 요청한 사용자 ID={}",
                empId, resultId, resultId, empId);

        // 1. 기본 정보 조회
        HrEvaluationDetailDto content = mapper.findEvaluationContent(resultId, empId);
        if (content == null) {
            log.error("HR 평가 상세 내역을 찾을 수 없음 - resultId={} for empId={}", resultId, empId);
            throw new HrException(ErrorCode.HR_EVALUATION_NOT_FOUND);
        }
        log.info("기본 평가 정보 조회 완료 - resultId={}", resultId);

        // 2. 등급 비율 정보
        RateInfo rateInfo = mapper.findRateInfo(resultId);
        if (rateInfo == null) {
            log.error("등급 비율 정보를 찾을 수 없음 - resultId={}", resultId);
            throw new HrException(ErrorCode.RATE_INFO_NOT_FOUND);
        }
        log.info("등급 비율 정보 조회 완료 - resultId={}", resultId);

        // 3. 가중치 정보
        WeightInfo weightInfo = mapper.findWeightInfo(resultId);
        if (weightInfo == null) {
            log.error("가중치 정보를 찾을 수 없음 - resultId={}", resultId);
            throw new HrException(ErrorCode.WEIGHT_INFO_NOT_FOUND);
        }
        log.info("가중치 정보 조회 완료 - resultId={}", resultId);

        // 4. 요인별 점수 목록
        List<FactorScoreDto> factorScores = mapper.findFactorScores(resultId);
        if (factorScores == null) {
            log.error("요인별 점수 목록을 찾을 수 없음 - resultId={}", resultId);
            throw new HrException(ErrorCode.FACTOR_SCORES_NOT_FOUND);
        }
        log.info("요인별 점수 목록 조회 완료 - factorScores.size={}", factorScores.size());

        // 5. 조립 후 반환
        HrEvaluationDetailResultDto result = HrEvaluationDetailResultDto.builder()
                .content(content)
                .rateInfo(rateInfo)
                .weightInfo(weightInfo)
                .factorScores(factorScores)
                .build();
        log.info("HR 평가 상세 내역 반환 완료 - resultId={}", resultId);

        return result;
    }

    // 평가 기준 정보를 조회하는 메서드 (최근 회차 기준으로 가져옴)
    public HrEvaluationCriteriaDto getEvaluationCriteria(Integer roundNo) {
        log.info("[EvaluationHrServiceImpl] getEvaluationCriteria() 호출 시작 - roundNo={}, 요청 파라미터={}", roundNo, roundNo);

        // 회차 번호가 주어지지 않으면 최신 회차로 설정
        int targetRoundNo = (roundNo != null) ? roundNo : mapper.findLatestRoundNo();
        if (targetRoundNo <= 0) {
            log.error("유효하지 않은 회차 번호 - roundNo={}", targetRoundNo);
            throw new HrException(ErrorCode.INVALID_ROUND_NO);
        }
        log.info("회차 번호 설정 완료 - targetRoundNo={}", targetRoundNo);

        RateInfo rate = mapper.findRateInfoByRoundNo(targetRoundNo);
        if (rate == null) {
            log.error("등급 비율 정보를 찾을 수 없음 - targetRoundNo={}", targetRoundNo);
            throw new HrException(ErrorCode.RATE_INFO_NOT_FOUND);
        }

        WeightInfo weight = mapper.findWeightInfoByRoundNo(targetRoundNo);
        if (weight == null) {
            log.error("가중치 정보를 찾을 수 없음 - targetRoundNo={}", targetRoundNo);
            throw new HrException(ErrorCode.WEIGHT_INFO_NOT_FOUND);
        }

        HrEvaluationCriteriaDto result = new HrEvaluationCriteriaDto(rate, weight);
        log.info("평가 기준 조회 완료 - targetRoundNo={}, rate={}, weight={}", targetRoundNo, rate, weight);

        return result;
    }

    // 페이지네이션 정보를 생성하는 메서드
    private Pagination buildPagination(int page, long total) {
        int totalPage = (int) Math.ceil((double) total / 10); // 기본 페이지 사이즈 10으로 설정
        return Pagination.builder()
                .currentPage(page)
                .totalPage(totalPage)
                .totalItems(total)
                .build();
    }
}
