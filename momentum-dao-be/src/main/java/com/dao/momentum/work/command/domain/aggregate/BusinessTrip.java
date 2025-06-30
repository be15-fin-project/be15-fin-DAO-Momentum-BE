package com.dao.momentum.work.command.domain.aggregate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "business_trip")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BusinessTrip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long businessTripId;

    private long approveId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TypeEnum type;

    @NotBlank
    private String place;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotBlank
    private String reason;

    private int cost;

    @Builder
    public BusinessTrip(
            Long approveId, TypeEnum type, String place, LocalDate startDate,
            LocalDate endDate, String reason, int cost
    ){
        this.approveId = approveId;
        this.type = type;
        this.place = place;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.cost = cost;
    }

}
