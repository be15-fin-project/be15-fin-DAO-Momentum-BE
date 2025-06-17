package com.dao.momentum.organization.company.command.domain.aggregate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @NotBlank
    private String email;

    @NotBlank
    private Integer paymentDay;

    @NotNull
    private LocalDate establishDate;

    @NotNull
    private LocalDateTime workStartTime;
}
