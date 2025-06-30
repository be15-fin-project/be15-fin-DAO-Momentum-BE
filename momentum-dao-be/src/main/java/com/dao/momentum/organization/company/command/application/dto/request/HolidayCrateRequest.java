package com.dao.momentum.organization.company.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class HolidayCrateRequest {
    @NotBlank
    private String holidayName;

    @NotNull
    LocalDate date;
}
