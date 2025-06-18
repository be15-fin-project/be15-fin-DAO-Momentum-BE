package com.dao.momentum.work.query.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Order {
    ASC, DESC;

    @JsonCreator // 소문자도 받을 수 있어 유연
    public static Order from(String value) {
        return Order.valueOf(value.toUpperCase());
    }

}
