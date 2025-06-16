package com.dao.momentum.work.command.domain.aggregate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "vacation_type")
public class VacationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int vacationTypeId;

    @NotBlank
    private String vacationType;

    @NotBlank
    private String description;
}