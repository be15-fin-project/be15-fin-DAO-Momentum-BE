package com.dao.momentum.work.command.domain.aggregate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "vacation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vacation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long vacationId;

    private int vacationTypeId;

    private long approveId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private String reason;

    @Builder
    public Vacation(int vacationTypeId, long approveId, LocalDate startDate,
                    LocalDate endDate, String reason) {
        this.vacationTypeId = vacationTypeId;
        this.approveId = approveId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
    }
}
