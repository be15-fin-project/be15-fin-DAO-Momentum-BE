package com.dao.momentum.organization.department.command.application.dto.response;

import com.dao.momentum.organization.department.command.application.dto.request.DepartmentUpdateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class DepartmentUpdateDTO {
    private String name;
    private String contact;
    private Long deptHeadId;

    public static DepartmentUpdateDTO from(DepartmentUpdateRequest request){
        return DepartmentUpdateDTO.builder()
                .name(request.getName())
                .contact(request.getContact())
                .deptHeadId(request.getDeptHeadId())
                .build();
    }
}
