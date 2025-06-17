package com.dao.momentum.organization.position.command.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PositionCreateResponse {
        private Integer positionId;
        private String message;
}
