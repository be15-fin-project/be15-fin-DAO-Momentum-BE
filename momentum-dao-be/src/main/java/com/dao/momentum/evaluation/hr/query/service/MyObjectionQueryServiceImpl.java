package com.dao.momentum.evaluation.hr.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.exception.HrException;
import com.dao.momentum.evaluation.hr.query.dto.request.MyObjectionListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.MyObjectionItemDto;
import com.dao.momentum.evaluation.hr.query.dto.response.MyObjectionListResultDto;
import com.dao.momentum.evaluation.hr.query.dto.request.MyObjectionRaw;
import com.dao.momentum.evaluation.hr.query.mapper.MyObjectionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyObjectionQueryServiceImpl implements MyObjectionQueryService {

    private final MyObjectionMapper mapper;

    @Override
    public MyObjectionListResultDto getMyObjections(Long empId, MyObjectionListRequestDto req) {
        // 1) 전체 개수 조회
        long total = mapper.countMyObjections(empId, req);
        if (total == 0) {
            throw new HrException(ErrorCode.MY_OBJECTIONS_NOT_FOUND);
        }

        // 2) 원시 점수 리스트 조회
        List<MyObjectionRaw> rawList = mapper.findMyObjections(empId, req);
        if (rawList == null || rawList.isEmpty()) {
            throw new HrException(ErrorCode.MY_OBJECTIONS_NOT_FOUND);
        }

        // 3) Raw → Item DTO 변환
        List<MyObjectionItemDto> content = rawList.stream()
                .map(raw -> MyObjectionItemDto.builder()
                        .objectionId(raw.getObjectionId())
                        .statusType(raw.getStatusType())
                        .createdAt(raw.getCreatedAt())
                        .overallGrade(toGrade(raw.getOverallScore()))
                        .build()
                )
                .collect(Collectors.toList());

        // 4) 페이지네이션 구성
        int totalPage = (int) Math.ceil((double) total / req.getSize());
        Pagination pagination = Pagination.builder()
                .currentPage(req.getPage())
                .totalItems(total)
                .totalPage(totalPage)
                .build();

        // 5) 결과 반환
        return new MyObjectionListResultDto(content, pagination);
    }

    private String toGrade(int score) {
        if (score >= 90) return "S";
        if (score >= 80) return "A";
        if (score >= 70) return "B";
        if (score >= 60) return "C";
        return "D";
    }
}
