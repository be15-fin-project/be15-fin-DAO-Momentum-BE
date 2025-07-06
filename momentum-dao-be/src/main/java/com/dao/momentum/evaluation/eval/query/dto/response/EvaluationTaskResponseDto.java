package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Schema(description = "평가 작성 대상 항목 DTO")
@Builder
public record EvaluationTaskResponseDto(

        @Schema(description = "회차 번호", example = "2")
        Integer roundNo,

        @Schema(description = "평가 유형 ID (PEER/UPWARD/DOWNWARD/ORG/SELF)")
        Integer typeId,

        @Schema(description = "평가 유형 이름 (PEER/UPWARD/DOWNWARD/ORG/SELF)")
        String typeName,

        @Schema(description = "평가 양식 ID", example = "3")
        Integer formId,

        @Schema(description = "평가 양식 이름", example = "조직 몰입 척도")
        String formName,

        @Schema(description = "부서 ID", example = "10")
        Integer deptId,

        @Schema(description = "평가 대상자 사원 번호 (자가/조직 평가는 null)")
        Long targetEmpId,

        @Schema(description = "평가 대상자 사번 (자가/조직 평가는 null)")
        String targetEmpNo,

        @Schema(description = "평가 대상자 이름 (자가/조직 평가는 자신의 이름)")
        String targetName,

        @Schema(description = "제출 여부")
        boolean submitted,

        @Schema(description = "평가 회차 시작일 (optional)")
        LocalDate startAt
) { }
