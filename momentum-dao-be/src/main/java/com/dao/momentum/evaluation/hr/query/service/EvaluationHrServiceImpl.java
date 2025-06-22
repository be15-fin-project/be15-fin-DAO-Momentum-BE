package com.dao.momentum.evaluation.hr.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.hr.query.dto.request.MyHrEvaluationListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.FactorScoreDto;
import com.dao.momentum.evaluation.hr.query.dto.response.HrEvaluationItemDto;
import com.dao.momentum.evaluation.hr.query.dto.response.HrEvaluationListResultDto;
import com.dao.momentum.evaluation.hr.query.mapper.EvaluationHrMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationHrServiceImpl implements EvaluationHrService {

    private final EvaluationHrMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public HrEvaluationListResultDto getHrEvaluations(long empId, MyHrEvaluationListRequestDto req) {
        // 1) 평가 내역 조회
        List<HrEvaluationItemDto> items =
            mapper.findHrEvaluations(empId, req);

        // 2) 전체 건수 조회
        long total =
            mapper.countHrEvaluations(empId, req);

        // 3) 각 평가별 요인 점수 조회 및 합치기
        List<FactorScoreDto> factorScores = new ArrayList<>();
        for (HrEvaluationItemDto item : items) {
            // HrEvaluationItemDto에 resultId 필드가 있어야 합니다.
            factorScores.addAll(
                mapper.findFactorScores(item.getResultId())
            );
        }

        // 4) 페이지네이션 정보 생성
        Pagination pagination = buildPagination(req.getPage(), req.getSize(), total);

        // 5) 결과 DTO 조립
        return HrEvaluationListResultDto.builder()
                .items(items)
                .factorScores(factorScores)
                .pagination(pagination)
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
