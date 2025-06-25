package com.dao.momentum.organization.department.query.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class DepartmentDetailDTO {
    private String name;
    private String contact;
    private LocalDate createdAt;
}
