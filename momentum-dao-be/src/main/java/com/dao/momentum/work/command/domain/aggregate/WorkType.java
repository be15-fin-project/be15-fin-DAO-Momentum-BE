package com.dao.momentum.work.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Entity
@Table(name = "work_type")
public class WorkType {
    @Id
    private int typeId;

    @Enumerated(EnumType.STRING)
    private WorkTypeName typeName;

    private Integer parentTypeId;

}
