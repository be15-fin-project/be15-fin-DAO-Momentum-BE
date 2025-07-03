package com.dao.momentum.evaluation.hr.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.exception.HrException;
import com.dao.momentum.evaluation.hr.query.dto.request.HrObjectionListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.*;
import com.dao.momentum.evaluation.hr.query.mapper.HrObjectionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HrObjectionQueryServiceImpl implements HrObjectionQueryService {

    private final HrObjectionMapper mapper;

    // 인사 평가 이의제기 목록 조회
    @Override
    public HrObjectionListResultDto getObjections(HrObjectionListRequestDto req) {
        // 전체 건수 조회
        long total = mapper.countObjections(req);

        // 페이징 조회
        List<HrObjectionItemDto> list = mapper.findObjections(req);

        if (list == null) {
            throw new HrException(ErrorCode.HR_OBJECTIONS_NOT_FOUND);
        }

        // Pagination 생성
        int totalPage = (int) Math.ceil((double) total / req.getSize());
        Pagination pagination = Pagination.builder()
                .currentPage(req.getPage())
                .totalPage(totalPage)
                .totalItems(total)
                .build();

        return new HrObjectionListResultDto(list, pagination);
    }

    // 인사 평가 이의 제기 상세 조회

    @Override
    public ObjectionDetailResultDto getObjectionDetail(Long objectionId) {
        // 1) 기본 상세 정보 조회
        ObjectionItemDto base = mapper.findObjectionDetail(objectionId);
        if (base == null) {
            throw new HrException(ErrorCode.MY_OBJECTIONS_NOT_FOUND);
        }

        Long resultId = base.getResultId();

        // 2) 요인별 점수 조회
        List<FactorScoreDto> scores = mapper.findFactorScores(resultId);

        // 3) 가중치 정보 조회
        WeightInfo weightInfo = mapper.findWeightInfo(resultId);

        // 4) 등급 비율 정보 조회
        RateInfo rateInfo = mapper.findRateInfo(resultId);

        // 5) 조립
        return ObjectionDetailResultDto.builder()
                .itemDto(base)
                .factorScores(scores)
                .weightInfo(weightInfo)
                .rateInfo(rateInfo)
                .build();
    }

}
