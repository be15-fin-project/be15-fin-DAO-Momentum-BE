package com.dao.momentum.organization.position.query.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionDto {
    private Integer positionId;
    private String name;
    private Integer level;
}
