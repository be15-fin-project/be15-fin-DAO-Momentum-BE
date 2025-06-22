package com.dao.momentum.evaluation.hr.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.exception.HrException;
import com.dao.momentum.evaluation.hr.query.dto.request.HrObjectionListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.HrObjectionListResponseDto;
import com.dao.momentum.evaluation.hr.query.dto.response.HrObjectionListResultDto;
import com.dao.momentum.evaluation.hr.query.mapper.HrObjectionMapper;
import com.dao.momentum.evaluation.kpi.exception.KpiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HrObjectionQueryServiceImpl implements HrObjectionQueryService {

    private final HrObjectionMapper mapper;

    @Override
    public HrObjectionListResultDto getObjections(HrObjectionListRequestDto req) {
        // 전체 건수 조회
        long total = mapper.countObjections(req);

        // 페이징 조회
        List<HrObjectionListResponseDto> list = mapper.findObjections(req);

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
}
