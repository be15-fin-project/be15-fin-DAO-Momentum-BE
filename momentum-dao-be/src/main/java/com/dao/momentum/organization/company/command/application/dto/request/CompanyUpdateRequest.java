package com.dao.momentum.organization.company.command.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class CompanyUpdateRequest {
    @NotNull
    private Integer companyId;

    @NotBlank
    private String name;

    @NotBlank
    private String chairman;

    @NotBlank
    private String address;

    @NotBlank
    private String contact;

    @NotBlank
    private String businessRegistrationNumber;

    @Email
    private String email;

    @Min(1)
    @Max(31)
    @NotNull
    private Integer paymentDay;

    @NotNull
    private LocalDate establishDate;

    @NotNull
    private LocalTime workStartTime;

    public CompanyUpdateDTO toDTO() {
        return new CompanyUpdateDTO(
                companyId,
                name,
                chairman,
                address,
                contact,
                businessRegistrationNumber,
                email,
                paymentDay,
                establishDate,
                workStartTime
        );
    }
}
