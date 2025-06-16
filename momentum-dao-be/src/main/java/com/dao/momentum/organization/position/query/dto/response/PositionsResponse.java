package com.dao.momentum.organization.position.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class PositionsResponse {
    private List<PositionDto> positionDtoList;
}
