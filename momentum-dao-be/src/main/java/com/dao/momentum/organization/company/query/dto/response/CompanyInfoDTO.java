package com.dao.momentum.organization.company.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class CompanyInfoDTO {
    private Integer companyId;

    private String name;

    private String chairman;

    private String address;

    private String contact;

    private String businessRegistrationNumber;

    private String email;

    private Integer paymentDay;

    private LocalDate establishDate;

    private LocalTime workStartTime;

    private Integer employeeCount;

    private Integer foundedYears;

    private Integer deptCount;
}
