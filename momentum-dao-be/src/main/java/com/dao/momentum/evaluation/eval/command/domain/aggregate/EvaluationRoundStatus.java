package com.dao.momentum.evaluation.eval.command.domain.aggregate;

import java.time.LocalDate;

public enum EvaluationRoundStatus {
    BEFORE("진행 전"),
    IN_PROGRESS("진행 중"),
    DONE("완료");

    private final String description;

    EvaluationRoundStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEditable() {
        return this == BEFORE;
    }

    public static EvaluationRoundStatus from(LocalDate startAt, LocalDate endAt, LocalDate today) {
        if (today.isBefore(startAt)) return BEFORE;
        if (!today.isAfter(endAt)) return IN_PROGRESS;
        return DONE;
    }
}
