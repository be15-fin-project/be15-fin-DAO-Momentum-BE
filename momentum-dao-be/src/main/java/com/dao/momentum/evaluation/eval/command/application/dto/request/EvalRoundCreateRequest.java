package com.dao.momentum.evaluation.eval.command.application.dto.request;

import com.dao.momentum.evaluation.hr.command.application.dto.request.HrRateCreateDTO;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrWeightCreateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Schema(description = "다면 평가 회차 생성 요청")
public class EvalRoundCreateRequest {

    @Schema(description = "회차 번호", example = "5")
    private int roundNo;

    @Schema(description = "평가 시작일", example = "2025-07-01")
    private LocalDate startAt;

    // HrWeight fields
    private int performWt;
    private int teamWt;
    private int attitudeWt;
    private int growthWt;
    private int engagementWt;
    private int resultWt;

    // HrRate fields
    private int rateS;
    private int rateA;
    private int rateB;
    private int rateC;
    private int rateD;

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
