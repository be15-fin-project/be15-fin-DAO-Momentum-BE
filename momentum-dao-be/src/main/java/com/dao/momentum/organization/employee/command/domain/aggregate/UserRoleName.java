package com.dao.momentum.organization.employee.command.domain.aggregate;

public enum UserRoleName {
    MASTER,
    HR_MANAGER,
    MANAGER,
    EMPLOYEE,
    BOOKKEEPING;

    public static UserRoleName fromString(String string) {
        return valueOf(string.toUpperCase());
    }
}
