package com.dao.momentum.evaluation.hr.command.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HrWeightCreateDTO {

    private int performWt;
    private int teamWt;
    private int attitudeWt;
    private int growthWt;
    private int engagementWt;
    private int resultWt;

    @Builder
    public HrWeightCreateDTO(int performWt, int teamWt, int attitudeWt, int growthWt, int engagementWt, int resultWt) {
        this.performWt = performWt;
        this.teamWt = teamWt;
        this.attitudeWt = attitudeWt;
        this.growthWt = growthWt;
        this.engagementWt = engagementWt;
        this.resultWt = resultWt;
    }
}
