package com.dao.momentum.organization.company.command.domain.aggregate;

import com.dao.momentum.organization.company.command.application.dto.request.CompanyUpdateDTO;
import com.dao.momentum.organization.company.command.application.dto.request.CompanyUpdateRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

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

    @Email
    private String email;

    @Min(1)
    @Max(31)
    private int paymentDay;

    @NotNull
    private LocalDate establishDate;

    @NotNull
    private LocalTime workStartTime;

    public void updateFrom(CompanyUpdateDTO dto) {
        this.name = dto.getName();
        this.chairman = dto.getChairman();
        this.address = dto.getAddress();
        this.contact = dto.getContact();
        this.businessRegistrationNumber = dto.getBusinessRegistrationNumber();
        this.email = dto.getEmail();
        this.paymentDay = dto.getPaymentDay();
        this.establishDate = dto.getEstablishDate();
        this.workStartTime = dto.getWorkStartTime();
    }
}
