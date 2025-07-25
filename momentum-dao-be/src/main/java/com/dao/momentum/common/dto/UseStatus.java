// com.dao.momentum.common.type.UseStatus.java
package com.dao.momentum.common.dto;

import lombok.Getter;

@Getter
public enum UseStatus {
    Y, N;


    public boolean isDeleted() {
        return this == Y;
    }

    public boolean isNotDeleted() {
        return this == N;
    }
}
