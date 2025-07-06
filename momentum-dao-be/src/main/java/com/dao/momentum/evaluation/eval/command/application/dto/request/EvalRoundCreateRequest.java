package com.dao.momentum.evaluation.eval.command.application.dto.request;

import com.dao.momentum.evaluation.hr.command.application.dto.request.HrRateCreateDTO;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrWeightCreateDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "다면 평가 회차 생성 요청")
public record EvalRoundCreateRequest(

        @Schema(description = "회차 번호", example = "5")
        int roundNo,

        @Schema(description = "평가 시작일", example = "2025-07-01")
        LocalDate startAt,

        // HrWeight fields
        int performWt,
        int teamWt,
        int attitudeWt,
        int growthWt,
        int engagementWt,
        int resultWt,

        // HrRate fields
        int rateS,
        int rateA,
        int rateB,
        int rateC,
        int rateD
) {

    // 변환 메서드들
    public EvalRoundCreateDTO toRoundDto() {
        return EvalRoundCreateDTO.builder()
                .roundNo(roundNo)
                .startAt(startAt)
                .build();
    }

    public HrWeightCreateDTO toWeightDto() {
        return HrWeightCreateDTO.builder()
                .performWt(performWt)
                .teamWt(teamWt)
                .attitudeWt(attitudeWt)
                .growthWt(growthWt)
                .engagementWt(engagementWt)
                .resultWt(resultWt)
                .build();
    }

    public HrRateCreateDTO toRateDto() {
        return HrRateCreateDTO.builder()
                .rateS(rateS)
                .rateA(rateA)
                .rateB(rateB)
                .rateC(rateC)
                .rateD(rateD)
                .build();
    }
}
