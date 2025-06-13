package com.dao.momentum.organization.employee.command.application.dto.request;

import com.dao.momentum.organization.employee.command.domain.aggregate.employee.Gender;
import com.dao.momentum.organization.employee.command.domain.aggregate.employee.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;


import java.time.LocalDate;

@Getter
@Builder
public class EmployeeRegisterRequest {
    @NotBlank
    private String name;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotNull
    @Pattern(
            regexp = "^01[016789]\\d{7,8}$",
            message = "유효한 휴대폰 번호 형식이 아닙니다. 예: 01012345678"
    )
    private String contact;

    @NotBlank
    private String address;

    private Integer deptId;

    @NotNull
    private Integer positionId;

    @NotNull
    private String [] employeeRoles;

    @NotNull
    private Status status;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate joinDate;

    @NotNull
    private Integer remainingDayoffHours;

    @NotNull
    private Integer remainingRefreshDays;

    @NotNull(message = "성별은 필수입니다.")
    private Gender gender;

}
