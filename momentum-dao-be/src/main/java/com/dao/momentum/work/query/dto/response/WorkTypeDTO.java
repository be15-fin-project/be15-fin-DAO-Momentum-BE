package com.dao.momentum.work.query.dto.response;

import lombok.Getter;

@Getter
public class WorkTypeDTO {
    private int typeId;

    private String typeName;

    private Integer parentTypeId;
}
