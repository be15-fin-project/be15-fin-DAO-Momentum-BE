package com.dao.momentum.evaluation.eval.command.application.dto.request;

import com.dao.momentum.evaluation.hr.command.application.dto.request.HrRateUpdateDTO;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrWeightUpdateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Schema(description = "다면 평가 회차 수정 요청 DTO")
@Builder
public record EvalRoundUpdateRequest(

        @Schema(description = "회차 ID", example = "5")
        Integer roundId,

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
