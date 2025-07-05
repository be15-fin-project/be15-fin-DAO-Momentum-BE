package com.dao.momentum.evaluation.hr.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.exception.HrException;
import com.dao.momentum.evaluation.hr.query.dto.request.HrObjectionListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.*;
import com.dao.momentum.evaluation.hr.query.mapper.HrObjectionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HrObjectionQueryServiceImpl implements HrObjectionQueryService {

    private final HrObjectionMapper mapper;

    // 인사 평가 이의제기 목록 조회
    @Override
    public HrObjectionListResultDto getObjections(HrObjectionListRequestDto req) {
        log.info("[HrObjectionQueryServiceImpl] getObjections() 호출 시작 - request={}", req);

        // 전체 건수 조회
        long total = mapper.countObjections(req);
        log.info("전체 이의제기 건수 조회 완료 - total={}", total);

        // 페이징 조회
        List<HrObjectionItemDto> list = mapper.findObjections(req);
        if (list == null) {
            log.error("이의제기 목록을 찾을 수 없음 - request={}", req);
            throw new HrException(ErrorCode.HR_OBJECTIONS_NOT_FOUND);
        }
        log.info("이의제기 목록 조회 완료 - list.size={}", list.size());

        // Pagination 생성
        int totalPage = (int) Math.ceil((double) total / req.getSize());
        Pagination pagination = Pagination.builder()
                .currentPage(req.getPage())
                .totalPage(totalPage)
                .totalItems(total)
                .build();
        log.info("페이지네이션 정보 생성 완료 - currentPage={}, totalPage={}, totalItems={}",
                pagination.getCurrentPage(), pagination.getTotalPage(), pagination.getTotalItems());

        return new HrObjectionListResultDto(list, pagination);
    }

    // 인사 평가 이의 제기 상세 조회
    @Override
    public ObjectionDetailResultDto getObjectionDetail(Long objectionId) {
        log.info("[HrObjectionQueryServiceImpl] getObjectionDetail() 호출 시작 - objectionId={}", objectionId);

        // 1) 기본 상세 정보 조회
        ObjectionItemDto base = mapper.findObjectionDetail(objectionId);
        if (base == null) {
            log.error("이의제기 상세 정보를 찾을 수 없음 - objectionId={}", objectionId);
            throw new HrException(ErrorCode.MY_OBJECTIONS_NOT_FOUND);
        }
        log.info("이의제기 기본 정보 조회 완료 - objectionId={}", objectionId);

        Long resultId = base.getResultId();
        log.info("결과 ID 조회 완료 - resultId={}", resultId);

        // 2) 요인별 점수 조회
        List<FactorScoreDto> scores = mapper.findFactorScores(resultId);
        log.info("요인별 점수 조회 완료 - resultId={}, scores.size={}", resultId, scores.size());

        // 3) 가중치 정보 조회
        WeightInfo weightInfo = mapper.findWeightInfo(resultId);
        log.info("가중치 정보 조회 완료 - resultId={}", resultId);

        // 4) 등급 비율 정보 조회
        RateInfo rateInfo = mapper.findRateInfo(resultId);
        log.info("등급 비율 정보 조회 완료 - resultId={}", resultId);

        // 5) 조립 후 반환
        ObjectionDetailResultDto result = ObjectionDetailResultDto.builder()
                .itemDto(base)
                .factorScores(scores)
                .weightInfo(weightInfo)
                .rateInfo(rateInfo)
                .build();
        log.info("이의제기 상세 조회 완료 - objectionId={}", objectionId);

        return result;
    }
}
