package com.dao.momentum.organization.employee.command.application.dto.request;

import com.dao.momentum.organization.employee.query.dto.response.RecordType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class EmployeeRecordsUpdateRequest {
    private List<EmployeeRecordsItemRequest> insertItems;

    private List<Long> idsToDelete;

    @Getter
    @Builder
    public static class EmployeeRecordsItemRequest {
        private RecordType type;

        private String organization;

        private LocalDate startDate;

        private LocalDate endDate;

        private String name;
    }
}
