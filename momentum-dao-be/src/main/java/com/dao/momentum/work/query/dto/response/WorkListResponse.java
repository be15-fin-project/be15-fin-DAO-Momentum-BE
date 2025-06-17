package com.dao.momentum.work.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class WorkListResponse {
    private List<WorkDTO> works;

    private Pagination pagination;
}
