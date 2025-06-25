package com.dao.momentum.evaluation.eval.command.application.dto.request;

import com.dao.momentum.evaluation.hr.command.application.dto.request.HrRateUpdateDTO;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrWeightUpdateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Schema(description = "다면 평가 회차 수정 요청 DTO")
public class EvalRoundUpdateRequest {

    @Schema(description = "회차 ID", example = "5")
    private Integer roundId;

    @Schema(description = "회차 번호", example = "5")
    private int roundNo;

    @Schema(description = "평가 시작일", example = "2025-07-01")
    private LocalDate startAt;

    // HrWeight
    private int performWt;
    private int teamWt;
    private int attitudeWt;
    private int growthWt;
    private int engagementWt;
    private int resultWt;

    // HrRate
    private int rateS;
    private int rateA;
    private int rateB;
    private int rateC;
    private int rateD;

    // 변환 메서드
    public EvalRoundUpdateDTO toRoundDto(Integer roundId) {
        return EvalRoundUpdateDTO.builder()
                .roundId(roundId)
                .roundNo(this.roundNo)
                .startAt(this.startAt)
                .build();
    }

    public HrWeightUpdateDTO toWeightDto() {
        return HrWeightUpdateDTO.builder()
                .performWt(performWt)
                .teamWt(teamWt)
                .attitudeWt(attitudeWt)
                .growthWt(growthWt)
                .engagementWt(engagementWt)
                .resultWt(resultWt)
                .build();
    }

    public HrRateUpdateDTO toRateDto() {
        return HrRateUpdateDTO.builder()
                .rateS(rateS)
                .rateA(rateA)
                .rateB(rateB)
                .rateC(rateC)
                .rateD(rateD)
                .build();
    }
}
