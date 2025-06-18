package com.dao.momentum.work.command.domain.aggregate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Entity
@Table(name = "vacation_type")
public class VacationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int vacationTypeId;

    @Enumerated(EnumType.STRING)
    private VacationTypeEnum vacationType;

    @NotBlank
    private String description;
}