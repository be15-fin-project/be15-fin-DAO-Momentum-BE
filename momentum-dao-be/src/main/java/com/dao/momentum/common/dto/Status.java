package com.dao.momentum.common.dto;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum Status {
    PENDING(1),
    ACCEPTED(2),
    REJECTED(3);

    private final int id;

    Status(int id) {
        this.id = id;
    }

    private static final Map<Integer, Status> ID_MAP = Stream.of(values())
            .collect(Collectors.toMap(Status::getId, e -> e));

    public static Status fromId(int id) {
        return ID_MAP.get(id);
    }
}