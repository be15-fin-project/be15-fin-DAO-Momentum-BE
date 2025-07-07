package com.dao.momentum.work.query.dto.response;

import lombok.Getter;

@Getter
public enum WorkTypeName {
    WORK("근무"),
    REMOTE_WORK("재택근무"),
    VACATION("휴가"),
    OVERTIME("연장근무"),
    NIGHT("야간근무"),
    HOLIDAY("휴일근무"),
    BUSINESS_TRIP("출장"),
    ADDITIONAL("초과근무");

    private final String description;

    WorkTypeName(String description) {
        this.description = description;
    }

    public String getTypeName() {
        return name();
    }

}