package com.dao.momentum.organization.employee.command.application.dto.request;

import com.dao.momentum.organization.employee.command.domain.aggregate.Gender;
import com.dao.momentum.organization.employee.command.domain.aggregate.Status;
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
    @NotBlank(message = "이름은 필수 항목입니다.")
    private String name;

    @NotNull(message = "생년월일은 필수 항목입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotNull(message = "연락처는 필수 항목입니다.")
    @Pattern(
            regexp = "\\d{2,3}-\\d{3,4}-\\d{4}",
            message = "유효한 전화 번호 형식이 아닙니다. 예: 010-1234-5678"
    )
    private String contact;

    @NotBlank(message = "주소는 필수 항목입니다.")
    private String address;

    private Integer deptId;

    @NotNull(message = "직위는 필수 항목입니다.")
    private Integer positionId;

    @NotNull(message = "재직 상태는 필수 항목입니다.")
    private Status status;

    @NotNull(message = "입사일은 필수 항목입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate joinDate;

    @NotNull(message = "부여 연차 시간은 필수 항목입니다.")
    private Integer remainingDayoffHours;

    @NotNull(message = "부여 리프레시 휴가 일수는 필수 항목입니다.")
    private Integer remainingRefreshDays;

    @NotNull(message = "성별은 필수 항목입니다.")
    private Gender gender;

}
