package com.dao.momentum.evaluation.hr.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.exception.HrException;
import com.dao.momentum.evaluation.hr.query.dto.request.MyObjectionListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.*;
import com.dao.momentum.evaluation.hr.query.dto.request.MyObjectionRaw;
import com.dao.momentum.evaluation.hr.query.mapper.MyObjectionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyObjectionQueryServiceImpl implements MyObjectionQueryService {

    private final MyObjectionMapper mapper;

    @Override
    public MyObjectionListResultDto getMyObjections(Long empId, MyObjectionListRequestDto req) {
        long total = mapper.countMyObjections(empId, req);
        if (total == 0) {
            throw new HrException(ErrorCode.MY_OBJECTIONS_NOT_FOUND);
        }

        List<MyObjectionRaw> rawList = mapper.findMyObjections(empId, req);
        if (rawList == null || rawList.isEmpty()) {
            throw new HrException(ErrorCode.MY_OBJECTIONS_NOT_FOUND);
        }

        List<MyObjectionItemDto> content = rawList.stream()
                .map(raw -> MyObjectionItemDto.builder()
                        .objectionId(raw.getObjectionId())
                        .statusType(raw.getStatusType())
                        .createdAt(raw.getCreatedAt())
                        .overallGrade(toGrade(raw.getOverallScore()))
                        .build()
                )
                .collect(Collectors.toList());

        int totalPage = (int) Math.ceil((double) total / req.getSize());
        Pagination pagination = Pagination.builder()
                .currentPage(req.getPage())
                .totalItems(total)
                .totalPage(totalPage)
                .build();

        return new MyObjectionListResultDto(content, pagination);
    }

    private String toGrade(int score) {
        if (score >= 90) return "S";
        if (score >= 80) return "A";
        if (score >= 70) return "B";
        if (score >= 60) return "C";
        return "D";
    }

    @Override
    public ObjectionDetailResultDto getObjectionDetail(Long empId, Long objectionId) {
        // 1) 기본 상세 정보 조회 (ObjectionListResultDto 하나)
        ObjectionListResultDto base = mapper.findObjectionDetail(empId, objectionId);
        if (base == null) {
            throw new HrException(ErrorCode.MY_OBJECTIONS_NOT_FOUND);
        }

        // 2) 요인별 점수 조회
        List<FactorScoreDto> scores = mapper.findFactorScores(base.getResultId());

        // 3) 결과 합치기
        return ObjectionDetailResultDto.builder()
                .list(Collections.singletonList(base))
                .factorScores(scores)
                .build();
    }
}
