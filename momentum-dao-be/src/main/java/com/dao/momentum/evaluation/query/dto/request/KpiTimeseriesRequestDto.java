package com.dao.momentum.evaluation.query.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KpiTimeseriesRequestDto {
    private Integer year; // nullable, default는 현재 연도
}
