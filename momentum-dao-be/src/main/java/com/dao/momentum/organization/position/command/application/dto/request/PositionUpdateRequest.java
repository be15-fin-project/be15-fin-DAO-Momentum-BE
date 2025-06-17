package com.dao.momentum.organization.position.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PositionUpdateRequest {
    @NotNull
    private Integer positionId;

    @NotBlank
    private String name;

    @NotNull
    private Integer level;
}
