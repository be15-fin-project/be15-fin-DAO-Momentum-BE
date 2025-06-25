package com.dao.momentum.organization.employee.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentMemberDTO {
    private String name;
    private String position;
    private String email;
    private String contact;
}
