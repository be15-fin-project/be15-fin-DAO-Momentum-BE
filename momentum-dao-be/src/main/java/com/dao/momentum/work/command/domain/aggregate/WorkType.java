package com.dao.momentum.work.command.domain.aggregate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "work_type")
public class WorkType {
    @Id
    private int typeId;

    @NotBlank
    private String typeName;
}
