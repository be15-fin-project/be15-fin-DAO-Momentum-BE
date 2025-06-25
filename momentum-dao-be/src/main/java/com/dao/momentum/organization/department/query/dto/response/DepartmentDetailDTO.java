package com.dao.momentum.organization.department.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentDetailDTO {
    private String name;
    private String contact;
    private LocalDate createdAt;
}
