package com.dao.momentum.work.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "work_type")
public class WorkType {
    @Id
    private int typeId;

    @Enumerated(EnumType.STRING)
    private WorkTypeName typeName;
}
