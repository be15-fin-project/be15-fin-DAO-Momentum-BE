package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "평가 작성 대상 항목 DTO")
public class EvaluationTaskResponseDto {
    @Schema(description = "회차 번호", example = "2")
    private int roundNo;

    @Schema(description = "평가 유형 이름 (PEER/UPWARD/DOWNWARD/ORG/SELF)")
    private String typeName;

    @Schema(description = "평가 양식 ID", example = "3")
    private int formId;

    @Schema(description = "평가 양식 이름", example = "조직 몰입 척도")
    private String formName;

    @Schema(description = "부서 ID", example = "10")
    private int deptId;

    @Schema(description = "평가 대상자 사원 No (자가/조직 평가는 null)")
    private String targetEmpNo;

    @Schema(description = "평가 대상자 이름 (사가 self/org는 자신의 이름)")
    private String targetName;

    @Schema(description = "제출 여부")
    private boolean submitted;

    @Schema(description = "평가 회차 시작일 (optional)")
    private LocalDate startAt;
}