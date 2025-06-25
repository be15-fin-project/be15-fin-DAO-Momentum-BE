package com.dao.momentum.evaluation.hr.command.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HrRateCreateDTO {

    private int rateS;
    private int rateA;
    private int rateB;
    private int rateC;
    private int rateD;

    @Builder
    public HrRateCreateDTO(int rateS, int rateA, int rateB, int rateC, int rateD) {
        this.rateS = rateS;
        this.rateA = rateA;
        this.rateB = rateB;
        this.rateC = rateC;
        this.rateD = rateD;
    }
}
