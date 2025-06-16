package com.dao.momentum.organization.position.query.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PositionDto {
    private Integer positionId;
    private String name;
    private Integer level;
}
