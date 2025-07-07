package com.dao.momentum.work.query.dto.request;

public enum Order {
    ASC, DESC;

    public static Order from(String value) {
        return Order.valueOf(value.toUpperCase());
    }

}
