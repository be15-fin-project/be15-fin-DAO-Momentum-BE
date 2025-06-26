package com.dao.momentum.organization.employee.command.application.dto.request;

import com.dao.momentum.organization.employee.command.domain.aggregate.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyInfoUpdateRequest {
    @NotBlank
    private String name;

    @NotNull
    private Gender gender;

    @NotBlank
    private String address;

    @Pattern(regexp = "\\d{2,3}-\\d{3,4}-\\d{4}", message = "유효하지 않은 연락처 형식입니다.")
    private String contact;
}
