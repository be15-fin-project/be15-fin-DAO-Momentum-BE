package com.dao.momentum.work.query.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class WorkListResponse {
    private List<WorkDTO> works;
}
