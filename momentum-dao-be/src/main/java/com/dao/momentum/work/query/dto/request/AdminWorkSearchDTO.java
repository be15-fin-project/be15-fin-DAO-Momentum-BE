package com.dao.momentum.work.query.dto.request;

import com.dao.momentum.work.query.dto.response.IsNormalWork;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AdminWorkSearchDTO {
    private String empNo;

    private String empName;

    private Integer deptId;

    private Integer positionId;

    private LocalDate rangeStartDate;

    private LocalDate rangeEndDate;

    private Integer typeId;

    private Integer childTypeId;

    private Integer vacationTypeId;

    private Order order;

    private IsNormalWork isNormalWork;

    private Integer page;

    private Integer size;

    private int getOffset() {
        return (page - 1) * size;
//        int page = getPage() == null ? 1 : getPage();
//        return (page - 1) * getLimit();
    }

    private int getLimit() {
        return getSize() == null ? 10 : getSize();
    }

    public static AdminWorkSearchDTO fromRequest(AdminWorkSearchRequest request) {
        LocalDate rangeEndDate = request.getRangeEndDate();
        Integer page = request.getPage();
        Integer size = request.getSize();

        return AdminWorkSearchDTO.builder()
                .empNo(request.getEmpNo())
                .empName(request.getEmpName())
                .deptId(request.getDeptId())
                .positionId(request.getPositionId())
                .rangeStartDate(request.getRangeStartDate())
                .rangeEndDate(rangeEndDate == null ?
                        null : rangeEndDate.plusDays(1))
                .typeId(request.getTypeId())
                .childTypeId(request.getChildTypeId())
                .vacationTypeId(request.getVacationTypeId())
                .order(request.getOrder())
                .isNormalWork(request.getIsNormalWork())
                .page(page == null ? 1 : page)
                .size(size == null ? 10 : size)
                .build();
    }
}
