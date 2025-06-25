package com.dao.momentum.organization.employee.query.dto.request;

import com.dao.momentum.approve.query.dto.approveTypeDTO.VacationDTO;
import com.dao.momentum.organization.employee.query.dto.response.VacationTimesDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class VacationComputeDTO {
    private int joinYear;

    private int joinMonth;

    private Integer targetYear;

    public static VacationComputeDTO fromRequest(VacationComputeRequest request) {
        return VacationComputeDTO.builder()
                .joinYear(request.getJoinYear())
                .joinMonth(request.getJoinMonth())
                .targetYear(request.getTargetYear() == null ?
                        LocalDate.now().getYear() : request.getTargetYear())
                .build();
    }
}
