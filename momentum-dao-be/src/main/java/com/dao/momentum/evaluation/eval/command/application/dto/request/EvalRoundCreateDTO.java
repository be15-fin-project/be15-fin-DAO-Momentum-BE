package com.dao.momentum.evaluation.eval.command.application.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class EvalRoundCreateDTO {
    private final int roundNo;
    private final LocalDate startAt;
}
