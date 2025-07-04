package com.dao.momentum.retention.prospect.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Retention 지원용 원시 데이터 DTO")
public class RetentionSupportRaw {

    @Schema(description = "근속 전망 Id", example = "31")
    private Long retentionId;

    @Schema(description = "사번", example = "20250001")
    private String empNo;

    @Schema(description = "사원 이름", example = "김현우")
    private String empName;

    @Schema(description = "부서명", example = "기획팀")
    private String deptName;

    @Schema(description = "직위명", example = "대리")
    private String positionName;

    @Schema(description = "Retention 점수 (등급/유형 변환용)", example = "85")
    private int retentionScore;

    @Schema(description = "요약 코멘트", example = "적극적으로 업무에 참여함")
    private String summaryComment;

    @Schema(description = "평가 회차 번호", example = "2")
    private int roundNo;

}
