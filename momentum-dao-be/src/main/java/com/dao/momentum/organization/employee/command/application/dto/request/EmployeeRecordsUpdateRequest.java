package com.dao.momentum.organization.employee.command.application.dto.request;

import com.dao.momentum.organization.employee.command.domain.aggregate.RecordType;
import jakarta.validation.constraints.NotNull;
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
        @NotNull
        private RecordType type;

        private String organization;

        @NotNull
        private LocalDate startDate;

        private LocalDate endDate;

        private String name;
    }
}
