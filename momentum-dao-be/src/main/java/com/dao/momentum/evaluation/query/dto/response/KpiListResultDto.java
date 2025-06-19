package com.dao.momentum.evaluation.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class KpiListResultDto {
    private List<KpiListResponseDto> content;
    private Pagination pagination;
}
